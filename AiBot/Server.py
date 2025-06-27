# Copyright 2025 Astronavigate
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from llama_cpp import Llama
from fastapi import FastAPI, Request, HTTPException
from fastapi.responses import StreamingResponse, JSONResponse
from pydantic import BaseModel
from typing import Generator
from fastapi.concurrency import run_in_threadpool
import asyncio
import os

# --- 模型加载 ---
MODEL_PATH = "model/DeepSeek-R1-Distill-Llama-8B-Q8_0.gguf"

# 检查模型文件是否存在
if not os.path.exists(MODEL_PATH):
    print(f"Error: Model file not found at {MODEL_PATH}")
    print("Please check the path or download the model.")

try:
    # 加载模型到 GPU
    model = Llama(
        model_path=MODEL_PATH,
        n_ctx=8192,
        n_gpu_layers=-1,
        n_threads=8,
    )
    print(f"Model {MODEL_PATH} loaded successfully!")
except Exception as e:
    print(f"Error loading model: {e}")
    print("Please ensure the model path is correct, the model file is valid,")
    print("and you have enough GPU/CPU memory.")
    raise

# --- FastAPI 应用初始化 ---
app = FastAPI()

# --- 请求体模型定义 ---
class PromptRequest(BaseModel):
    prompt: str  # 用户输入的提示词
    max_tokens: int = 131072
    stream: bool = False # 控制是否流式返回

# --- 中断标志 ---
interruption_flag = asyncio.Event()

# --- 非流式生成接口 ---
@app.post("/generate")
async def generate(req: PromptRequest):

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
                text = chunk.get("choices", [{}])[0].get("text", "")

                if text:
                    yield f"data: {text}\n\n"

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

    return StreamingResponse(generate_stream_async(), media_type="text/event-stream")

# --- 中断流式生成接口 ---
@app.post("/interrupt_stream")
async def interrupt_stream():
    interruption_flag.set() # 设置中断标志
    print("Interrupt signal received and set.")
    return JSONResponse({"message": "Interruption signal sent."}) # 返回确认信息

# --- 应用启动入口 ---
if __name__ == "__main__":
    import uvicorn
    print(f"Starting Uvicorn server on http://0.0.0.0:2268")
    uvicorn.run(app, host="0.0.0.0", port=2268)
