import json
import os
import uuid

import torch
import whisper
from langchain_community.embeddings import LlamaCppEmbeddings
from langchain_community.llms import LlamaCpp
from langchain_core.prompts import PromptTemplate
from langchain_weaviate import WeaviateVectorStore
from weaviate.classes.query import Filter
from whisper.utils import get_writer

import weaviate

# 静态配置
EMBED_PATH = r"models\mixedbread-ai\mxbai-embed-large-v1\mxbai-embed-large-v1-f16.gguf"

# DOC CPU GPU SPD #
# 0 0 0 0 ERR # LLM_PATH = r"models\arcee-ai\Llama-3.1-SuperNova-Lite-GGUF\supernova-lite-v1.Q4_K_M.gguf "

# 8 10 3 6 # LLM_PATH = r"models\lmstudio-community\EXAONE-3.5-7.8B-Instruct-GGUF\EXAONE-3.5-7.8B-Instruct-Q4_K_M.gguf "
# 6 9 9 7 # LLM_PATH = r"models\lmstudio-community\Mistral-7B-Instruct-v0.3-GGUF\Mistral-7B-Instruct-v0.3-Q4_K_M.gguf "
# 0 0 0 0 ERR # LLM_PATH = r"models\lmstudio-community\Phi-4-reasoning-plus-GGUF\Phi-4-reasoning-plus-Q4_K_M.gguf "
# 6 10 4 10 # LLM_PATH = r"models\lmstudio-community\Phi-4-mini-reasoning-GGUF\Phi-4-mini-reasoning-Q4_K_M.gguf "

# 0 0 0 5 # LLM_PATH = r"models\microsoft\phi-4-gguf\phi-4-TQ2_0.gguf "
# 8 10 10 4 #
LLM_PATH = r"models\microsoft\phi-4-gguf\phi-4-Q3_K_S.gguf "
# 8 10 10 2 # LLM_PATH = r"models\microsoft\phi-4-gguf\phi-4-Q4_0.gguf "

# 0 0 0 0 ERR # LLM_PATH = r"models\mistralai\Ministral-3-8B-Instruct-2512-GGUF\Ministral-3-8B-Instruct-2512-Q4_K_M.gguf"

# 7 7 6 10 # LLM_PATH = r"models\mradermacher\Llama3.3-8B-Instruct-Thinking-Heretic-Uncensored-Claude-4.5-Opus-High-Reasoning-i1-GGUF\Llama3.3-8B-Instruct-Thinking-Heretic-Uncensored-Claude-4.5-Opus-High-Reasoning.i1-Q4_K_M.gguf "

# 2 1 0 10 # LLM_PATH = r"models\tiiuae\Falcon-H1-7B-Instruct-GGUF\Falcon-H1-7B-Instruct-Q2_K_S.gguf "
# 4 2 0 7 # LLM_PATH = r"models\tiiuae\Falcon-H1-7B-Instruct-GGUF\Falcon-H1-7B-Instruct-Q4_K_M.gguf "
# 4 2 0 5 # LLM_PATH = r"models\tiiuae\Falcon-H1-7B-Instruct-GGUF\Falcon-H1-7B-Instruct-Q8_0.gguf "
# 0 1 1 9 # LLM_PATH = r"models\tiiuae\Falcon-H1R-7B-GGUF\Falcon-H1R-7B-Q4_K_M.gguf "
# 0 1 1 3 # LLM_PATH = r"models\tiiuae\Falcon-H1R-7B-GGUF\Falcon-H1R-7B-Q8_0.gguf "

INDEX_NAME = "Unified_RAG_Index"
WEAVIATE_HOST = "172.23.173.58"
LLM_CL = 8192 # 16384 24576

def generate_uuid(self, table_name: str, table_id: str):
    return str(uuid.uuid5(uuid.NAMESPACE_DNS, f"{table_name}:{table_id}"))

def _detect_hardware():
    """硬件感知逻辑"""
    if torch.cuda.is_available():
        return {"n_gpu_layers": -1, "n_threads": 4, "desc": "GPU (CUDA) 加速"}
    return {"n_gpu_layers": 0, "n_threads": os.cpu_count() or 4, "desc": "CPU 阵列"}

hw_config = _detect_hardware()

class WeaviateService:
    def __init__(self):
        self.embeddings = self._init_embeddings()
        self.client = weaviate.connect_to_local(WEAVIATE_HOST)
        self.vector_store = WeaviateVectorStore(
            client=self.client,
            index_name=INDEX_NAME,
            text_key="content",
            embedding=self.embeddings
        )
        print(f"[*] 向量服务已就绪 | 模式: {hw_config['desc']}")

    def _init_embeddings(self):
        """加载本地 GGUF 模型"""
        return LlamaCppEmbeddings(
            model_path=EMBED_PATH,
            n_gpu_layers=hw_config["n_gpu_layers"],
            n_threads=hw_config["n_threads"],
            n_ctx=512,
            n_batch=512,
            verbose=False
        )

    def upsert_vector(self, table_id: str, table_name: str, content: str, metadata: dict):
        uid = self.generate_uuid(table_name, table_id)
        metadata.update({"origin_table": table_name, "origin_id": table_id})
        self.vector_store.add_texts(texts=[content], metadatas=[metadata], ids=[uid])
        return uid

    def delete_vector(self, table_id: str, table_name: str):
        uid = self.generate_uuid(table_name, table_id)
        self.vector_store.delete(ids=[uid])
        return uid

    def search(self, query: str, table_name: str = None, top_k: int = 3):
        search_kwargs = {"k": top_k}
        if table_name:
            search_kwargs["filters"] = Filter.by_property("origin_table").equal(table_name)
        return self.vector_store.similarity_search(query, **search_kwargs)

    def get_object(self, table_id: str, table_name: str):
        uid = self.generate_uuid(table_name, table_id)
        col = self.client.collections.get(INDEX_NAME)
        return col.query.fetch_object_by_id(uid), uid


class LLMService:
    def __init__(self, vector_service):
        self.vs = vector_service
        self.llm_path = LLM_PATH

        self.template = """Instructions:
You are a High-Dimensional Analytical Officer.
Carefully analyze all provided Attachments to answer the User's question.
Integrate content from the Attachments wherever relevant.
Do NOT respond with links only.
If the Attachments contain insufficient information, reason internally and indicate clearly.
Answer concisely, factually, and analytically.

Context:
{context}

{question}

Assistant:
"""
        self.prompt = PromptTemplate(
            template=self.template,
            input_variables=["context", "question"]
        )

    # --------------------------------------------------
    # 每次请求创建一个全新的 LLM（关键）
    # --------------------------------------------------
    def _new_llm(self):
        return LlamaCpp(
            model_path=self.llm_path,
            n_gpu_layers=hw_config["n_gpu_layers"],
            n_ctx=LLM_CL,
            n_batch=128,
            max_tokens=2048,
            temperature=0.7,
            repeat_penalty=1.15,
            top_p=0.95,
            use_mmap=True,
            use_mlock=False,
            verbose=False,
            stop=["User:", "</s>"]
        )

    # --------------------------------------------------
    # Prompt 构造（统一入口，方便你以后加 session）
    # --------------------------------------------------
    def _build_prompt(self, query: str, table_name: str = None, top_k: int = 5):
        docs = self.vs.search(query, table_name=table_name, top_k=top_k)
        context = "\n".join(
            [f"[{i + 1}] {d.page_content}" for i, d in enumerate(docs)]
        )
        prompt = self.prompt.format(context=context, question=query)
        return prompt, docs

    # --------------------------------------------------
    # 非流式接口（同步）
    # --------------------------------------------------
    def think_and_answer(self, query: str, table_name: str = None):
        llm = self._new_llm()
        prompt, docs = self._build_prompt(query, table_name, top_k=5)

        response = llm.client.create_completion(
            prompt=prompt,
            max_tokens=2048,
            temperature=0.7,
            repeat_penalty=1.15,
            top_p=0.95,
            stop=["User:", "</s>"]
        )

        answer = response["choices"][0]["text"].strip()

        return {
            "query": query,
            "answer": answer,
            "sources": [
                {"content": d.page_content, "metadata": d.metadata}
                for d in docs
            ]
        }

    # --------------------------------------------------
    # 流式接口（Generator）
    # --------------------------------------------------
    def stream_answer(self, query: str, table_name: str = None):
        llm = self._new_llm()
        prompt, docs = self._build_prompt(query, table_name, top_k=5)

        print(prompt)

        def generate():
            # 先返回元信息
            yield json.dumps({
                "type": "metadata",
                "sources": [d.metadata for d in docs]
            }) + "\n"

            stream = llm.client.create_completion(
                prompt=prompt,
                max_tokens=2048,
                temperature=0.7,
                repeat_penalty=1.15,
                top_p=0.95,
                stream=True,
                stop=["User:", "</s>"]
            )

            for chunk in stream:
                token = chunk["choices"][0]["text"]
                if token:
                    yield json.dumps({
                        "type": "token",
                        "content": token
                    }) + "\n"

        return generate


class WhisperService:
    def __init__(self, device=None):
        self.device = device or ("cuda" if torch.cuda.is_available() else "cpu")
        print(f"[*] 加载 Whisper 模型到 {self.device} ...")
        self.model = whisper.load_model("large", device=self.device)
        self.word_options = {
            "highlight_words": False,
            "max_line_count": 50,
            "max_line_width": 50
        }

    def transcribe_to_srt(self, audio_path: str):
        if not os.path.isfile(audio_path):
            raise FileNotFoundError(f"音频文件不存在: {audio_path}")

        # 转写
        result = self.model.transcribe(audio_path)

        # 输出 SRT 到同目录
        output_dir = os.path.dirname(audio_path)
        writer = get_writer("srt", output_dir)
        writer(result, audio_path, self.word_options)

        srt_path = os.path.splitext(audio_path)[0] + ".srt"
        return srt_path