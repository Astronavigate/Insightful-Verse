import os
import time
import subprocess
import atexit
import json
import requests
import torch
import whisper
import weaviate
import uuid
from langchain_openai import ChatOpenAI, OpenAIEmbeddings
from langchain_weaviate import WeaviateVectorStore
from whisper.utils import get_writer

# =========================
# 路径与硬件配置
# =========================
SERVER_EXE = r"llama\llama-b7907-bin-win-cuda-13.1-x64\llama-server.exe"
LLM_PATH = r"models\microsoft\phi-4-gguf\phi-4-Q3_K_S.gguf"
EMBED_PATH = r"models\mixedbread-ai\mxbai-embed-large-v1\mxbai-embed-large-v1-f16.gguf"

LLM_PORT, EMBED_PORT = 8090, 8091
INDEX_NAME = "Unified_RAG_Index"
WEAVIATE_HOST = "172.23.173.58"


def _detect_hardware():
    if torch.cuda.is_available():
        return {"n_gpu_layers": -1, "n_threads": 4, "desc": "GPU (CUDA) 加速"}
    return {"n_gpu_layers": 0, "n_threads": 8, "desc": "CPU 阵列"}


hw_config = _detect_hardware()


# =========================
# 自动化进程管理 (Orchestrator)
# =========================
class ModelManager:
    def __init__(self):
        self.procs = []
        atexit.register(self.kill_all)

    def ensure_services(self):
        """强制清理并重新唤醒算力引擎"""
        print("[-] 执行基础设施自检...")
        # 暴力清理旧进程，确保新参数（如扩大的 batch）生效
        try:
            subprocess.run(["taskkill", "/F", "/IM", "llama-server.exe", "/T"],
                           stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            time.sleep(1.5)
        except:
            pass

        self._boot_if_needed("Embedding Engine", EMBED_PATH, EMBED_PORT, is_embed=True)
        self._boot_if_needed("Inference Engine", LLM_PATH, LLM_PORT, is_embed=False)
        print("[+] 核心架构已锁定，算力就绪。")

    def _boot_if_needed(self, name, model, port, is_embed):
        # 此时端口理论上已释放
        print(f"[*] 正在拉起 {name} (Port {port})...")

        # 参数分发：mxbai 物理极限为 512，Phi-4 给予高上下文
        ctx_val = "512" if is_embed else "16384"
        batch_val = "512" if is_embed else "4096"

        cmd = [
            SERVER_EXE,
            "-m", model,
            "--port", str(port),
            "-ngl", str(hw_config["n_gpu_layers"]),
            "-c", ctx_val,
            "-b", batch_val,
            "--ubatch-size", batch_val,
            "--host", "127.0.0.1"
        ]
        if is_embed: cmd += ["--embedding"]

        proc = subprocess.Popen(
            cmd,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            creationflags=subprocess.CREATE_NO_WINDOW if os.name == 'nt' else 0
        )
        self.procs.append(proc)

        # 循环探测健康检查接口
        for _ in range(30):
            try:
                if requests.get(f"http://127.0.0.1:{port}/health", timeout=1).status_code == 200:
                    print(f"[+] {name} 在线。")
                    return
            except:
                pass
            time.sleep(1)
        raise RuntimeError(f"[-] {name} 启动超时，请检查显存。")

    def kill_all(self):
        for p in self.procs: p.terminate()


manager = ModelManager()


# =========================
# 向量服务 (Weaviate Service)
# =========================
class WeaviateService:
    def __init__(self):
        manager.ensure_services()
        self.embeddings = OpenAIEmbeddings(
            base_url=f"http://127.0.0.1:{EMBED_PORT}/v1",
            api_key="sk-ravon",
            check_embedding_ctx_length=False  # 禁用前端检查，由后端逻辑截断
        )
        self.client = weaviate.connect_to_local(WEAVIATE_HOST)
        self.vector_store = WeaviateVectorStore(self.client, INDEX_NAME, "content", self.embeddings)

    def generate_uuid(self, table_name, table_id):
        return str(uuid.uuid5(uuid.NAMESPACE_DNS, f"{table_name}:{table_id}"))

    def upsert_vector(self, table_id, table_name, content):
        metadata = {}
        uid = self.generate_uuid(table_name, table_id)
        metadata.update({"origin_table": table_name, "origin_id": table_id})
        # 插入时同样进行物理长度保护
        self.vector_store.add_texts([content[:1000]], [metadata], ids=[uid])
        return uid

    def delete_vector(self, table_id, table_name):
        uid = self.generate_uuid(table_name, table_id)
        try:
            self.vector_store.delete(ids=[uid])
            print(f"[+] 向量已清理: {table_name} (ID: {table_id}) -> UUID: {uid}")
            return uid
        except Exception as e:
            print(f"[-] 删除向量失败: {e}")
            return None

    def search(self, query, table_name=None, top_k=3):
        # [物理熔断] 强制截断，确保不触发 mxbai 512 token 报错
        safe_query = query[:800]
        search_kwargs = {"k": top_k}
        if table_name:
            from weaviate.classes.query import Filter
            search_kwargs["filters"] = Filter.by_property("origin_table").equal(table_name)
        return self.vector_store.similarity_search(safe_query, **search_kwargs)


# =========================
# 语言模型服务 (LLM Service)
# =========================
class LLMService:
    def __init__(self, vector_service):
        self.vs = vector_service
        self.llm = ChatOpenAI(
            base_url=f"http://127.0.0.1:{LLM_PORT}/v1",
            api_key="sk-ravon",
            streaming=True,
            temperature=0.3
        )

    def _refine_query(self, raw_query: str) -> str:
        """[语义压缩] 针对超长输入，利用 LLM 提炼检索意图"""
        if len(raw_query) < 600:
            return raw_query

        print(f"[*] 正在压缩高维输入意图...")
        refine_prompt = (
            "Summarize the following core request into a single precise search query. "
            "Focus on entities and main intent. Answer ONLY the query.\n\n"
            f"Input: {raw_query}\n\n"
            "Query:"
        )
        try:
            # 使用同步 invoke 快速提炼
            summary = self.llm.invoke(refine_prompt)
            refined = summary.content.strip()
            print(f"[+] 意图已锁定: {refined}")
            return refined
        except:
            return raw_query[:500]

    def _build_prompt(self, query, table_name=None, top_k=5):
        # 1. 语义预处理
        search_intent = self._refine_query(query)
        # 2. 向量检索
        docs = self.vs.search(search_intent, table_name, top_k)

        ctx = "\n".join([f"[{i + 1}] {d.page_content}" for i, d in enumerate(docs)])
        # """Instructions: You are a Deep-Vision Officer. Use the context to answer precisely.
        template = """Instructions: You name is Astronav. Use the context to answer precisely.
Carefully analyze all provided Attachments to answer the User's question.
Integrate content from the Attachments wherever relevant.
If the Attachments contain insufficient information, reason internally and indicate clearly.
Answer concisely, factually, and analytically.

Context:
{context}

Question: {question}
Assistant:"""
        return template.format(context=ctx, question=query), docs

    def think_and_answer(self, query: str, table_name: str = None):
        prompt, docs = self._build_prompt(query, table_name)

        try:
            print(f"[*] 正在生成完整响应...")
            response = self.llm.invoke(prompt)

            return {
                "type": "final_answer",
                "content": response.content,
                "sources": [d.metadata for d in docs],
                "query_refined": len(query) >= 600
            }
        except Exception as e:
            print(f"[-] 推理失败: {e}")
            return {"error": str(e), "type": "error"}

    def stream_answer(self, query: str, table_name: str = None):
        prompt, docs = self._build_prompt(query, table_name)

        def generate():
            yield json.dumps({"type": "metadata", "sources": [d.metadata for d in docs]}) + "\n"
            for chunk in self.llm.stream(prompt):
                if chunk.content:
                    yield json.dumps({"type": "token", "content": chunk.content}) + "\n"

        return generate


# =========================
# 语音识别服务 (Whisper Service)
# =========================
class WhisperService:
    def __init__(self):
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.model = whisper.load_model("large", device=self.device)

    def transcribe_to_srt(self, audio_path: str):
        result = self.model.transcribe(audio_path)
        output_dir = os.path.dirname(audio_path)
        writer = get_writer("srt", output_dir)
        writer(result, audio_path, {"highlight_words": False, "max_line_count": 50, "max_line_width": 50})
        return os.path.splitext(audio_path)[0] + ".srt"
