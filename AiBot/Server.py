from llama_cpp import Llama
from fastapi import FastAPI, Request, HTTPException
from fastapi.responses import StreamingResponse, JSONResponse
from pydantic import BaseModel
from typing import Generator
from fastapi.concurrency import run_in_threadpool
import asyncio
import os

# --- 模型加载 ---
# 确保模型路径正确，这里假设模型文件在项目根目录下的 'model/' 文件夹中
# 如果你的模型文件不在这个路径，请修改 model_path
MODEL_PATH = "model/DeepSeek-R1-Distill-Llama-8B-Q8_0.gguf"

# 检查模型文件是否存在
if not os.path.exists(MODEL_PATH):
    print(f"Error: Model file not found at {MODEL_PATH}")
    print("Please check the path or download the model.")
    # 你可以选择在这里退出程序或抛出异常
    # raise FileNotFoundError(f"Model file not found: {MODEL_PATH}")

try:
    # 加载模型到 GPU
    # n_ctx: 上下文窗口大小。DeepSeek R1 Distill Llama 官方文档建议上下文长度为 131072。
    #        然而，设置为这么大需要巨大的内存。2048 是一个常见的起点。
    # n_gpu_layers: 设置为 -1 表示将所有可用的模型层加载到 GPU。
    #               这是最通用和推荐的做法，避免了因层数不匹配导致的崩溃。
    # n_threads: CPU 线程数，用于 CPU 推理（当层未加载到 GPU 时）。
    model = Llama(
        model_path=MODEL_PATH,
        n_ctx=2048, # 注意：设置为 131072 会非常耗内存，通常不会这样设置。根据你的硬件调整。
        n_gpu_layers=-1, # 修复点：设置为 -1，将所有层加载到 GPU
        n_threads=8,
        # chat_format="chatml" # 如果模型明确支持 chatml 格式，可以在这里指定
    )
    print(f"Model {MODEL_PATH} loaded successfully!")
except Exception as e:
    print(f"Error loading model: {e}")
    print("Please ensure the model path is correct, the model file is valid,")
    print("and you have enough GPU/CPU memory.")
    # 如果模型加载失败，通常会希望程序退出
    raise

# --- FastAPI 应用初始化 ---
app = FastAPI()

# --- 请求体模型定义 ---
class PromptRequest(BaseModel):
    prompt: str  # 用户输入的提示词
    # 考虑模型实际的最大上下文长度，131072 通常只适用于模型内部，而不是每次生成
    # 把它设置为一个更合理的值，如 2048，以防意外生成过长的响应导致内存问题
    max_tokens: int = 32768
    stream: bool = False # 控制是否流式返回（虽然这里只处理流式，但保留兼容性）

# --- 中断标志 ---
# 用于控制流式生成中断的异步事件标志test_llama
# 适用于单用户或需要中断所有当前生成的情况。
# 对于多用户系统，可能需要更复杂的、基于请求ID或会话ID的标志管理。
interruption_flag = asyncio.Event()

# --- 非流式生成接口 (可选，如果不需要可以移除) ---
@app.post("/generate")
async def generate(req: PromptRequest):
    """
    非流式文本生成接口。
    将同步的模型调用放入线程池中运行，避免阻塞主事件循环。
    """
    # 确保在开始新的生成前清除中断标志
    interruption_flag.clear()

    try:
        resp = await run_in_threadpool(
            lambda: model.create_completion(
                prompt=req.prompt,
                max_tokens=req.max_tokens,
                stream=False
            )
        )
        return {"result": resp["choices"][0]["text"]}
    except Exception as e:
        print(f"Error in /generate: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {e}")

# --- 流式生成接口 ---
@app.post("/stream_generate")
async def stream_generate(req: PromptRequest):
    """
    流式文本生成接口。
    使用 Server-Sent Events (SSE) 协议，将 AI 模型的逐字输出实时推送到客户端。
    """
    async def generate_stream_async() -> Generator[str, None, None]:
        # 开始新的流式生成前，清除中断标志，确保不会被之前的请求影响
        interruption_flag.clear()
        print(f"Received stream request with prompt: {req.prompt[:50]}...") # 打印前50个字符

        try:
            # 在线程池中执行 llama 模型的流式生成，因为它是一个同步操作
            iterator = await run_in_threadpool(
                lambda: model.create_completion(
                    prompt=req.prompt,
                    max_tokens=req.max_tokens,
                    stream=True
                )
            )

            # 逐块迭代模型输出
            for chunk in iterator:
                # 检查中断标志是否被设置
                if interruption_flag.is_set():
                    print("Generation interrupted by client request (FastAPI side).")
                    yield "data: [STOP]\n\n" # 发送一个特殊标记给客户端，表示已中断
                    break # 跳出循环，停止生成

                # 提取文本块内容
                # Llama.cpp Python 库的 stream 响应结构可能略有不同，需要根据实际输出调整
                # 示例：chunk = {"choices": [{"text": "...", "index": 0}]}
                text = chunk.get("choices", [{}])[0].get("text", "")

                if text: # 仅在有实际文本时才发送，避免发送空事件
                    yield f"data: {text}\n\n" # 修复点：按照 SSE 协议格式化输出

                # 引入一个微小的异步延迟，允许事件循环处理其他任务（如中断信号）
                await asyncio.sleep(0.001)

            # 流正常结束时，发送一个结束事件（可选，但有助于客户端判断流的完成）
            yield "event: end\ndata: \n\n"
            print("Stream generation completed.")

        except Exception as e:
            # 捕获生成过程中的异常并发送错误信息
            print(f"Error during stream generation: {e}")
            yield f"data: [ERROR: {e}]\n\n" # 发送 SSE 格式的错误信息
        finally:
            # 无论生成是否完成或中断，都确保清除中断标志
            interruption_flag.clear()

    # 修复点：使用 StreamingResponse 返回异步生成器，并设置 media_type 为 text/event-stream
    return StreamingResponse(generate_stream_async(), media_type="text/event-stream")

# --- 中断流式生成接口 ---
@app.post("/interrupt_stream")
async def interrupt_stream():
    """
    设置中断标志，停止当前正在运行的流式生成。
    """
    interruption_flag.set() # 设置中断标志
    print("Interrupt signal received and set.")
    return JSONResponse({"message": "Interruption signal sent."}) # 返回确认信息

# --- 应用启动入口 ---
if __name__ == "__main__":
    import uvicorn
    # 运行 FastAPI 应用，监听所有网络接口 (0.0.0.0) 的 2268 端口
    print(f"Starting Uvicorn server on http://0.0.0.0:2268")
    uvicorn.run(app, host="0.0.0.0", port=2268)
