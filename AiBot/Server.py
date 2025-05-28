from llama_cpp import Llama
from fastapi import FastAPI, Request
from fastapi.responses import StreamingResponse, JSONResponse
from pydantic import BaseModel
from typing import Generator
from fastapi.concurrency import run_in_threadpool # 导入 run_in_threadpool

# 加载模型到 GPU
# 请注意，Llama 模型的加载本身是一个同步操作，但它只在应用启动时执行一次
model = Llama(
    model_path="model/DeepSeek-R1-Distill-Llama-8B-Q8_0.gguf",
    n_ctx=2048,
    n_gpu_layers=4096,
    n_threads=8
)

app = FastAPI()

class PromptRequest(BaseModel):
    prompt: str
    max_tokens: int = 131072
    stream: bool = False  # 控制是否流式返回


@app.post("/generate")
async def generate(req: PromptRequest):
    # 非流式返回完整结果
    # 将同步的模型调用放入线程池中运行
    resp = await run_in_threadpool(
        lambda: model(req.prompt, max_tokens=req.max_tokens, stream=False)
    )
    return {"result": resp["choices"][0]["text"]}


@app.post("/stream_generate")
async def stream_generate(req: PromptRequest):
    # 流式返回逐字输出
    async def generate_stream_async() -> Generator[str, None, None]:
        # 对于流式生成，我们可以让 generator 函数本身是异步的
        # 但 chunk 的获取仍然是同步的，所以也需要放入线程池
        # 这里需要稍微调整，因为 generator 无法直接 await run_in_threadpool
        # 我们将整个 for 循环的迭代器创建放入线程池
        print(req.prompt)
        iterator = await run_in_threadpool(
            lambda: model(req.prompt, max_tokens=req.max_tokens, stream=True)
        )
        for chunk in iterator:
            text = chunk.get("choices", [{}])[0].get("text", "")
            yield text

    # StreamingResponse 需要一个 generator，它可以是同步或异步的
    return StreamingResponse(generate_stream_async(), media_type="text/plain")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=2268)
