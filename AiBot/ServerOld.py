from llama_cpp import Llama
from fastapi import FastAPI, Request
from fastapi.responses import StreamingResponse, JSONResponse
from pydantic import BaseModel
from typing import Generator

# 加载模型到 GPU
model = Llama(
    model_path="model/Astronav-R1-Distill-Llama-8B-Q8_0.gguf",
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
    resp = model(req.prompt, max_tokens=req.max_tokens, stream=False)
    return {"result": resp["choices"][0]["text"]}


@app.post("/stream_generate")
async def stream_generate(req: PromptRequest):
    # 流式返回逐字输出
    def generate_stream() -> Generator[str, None, None]:
        for chunk in model(req.prompt, max_tokens=req.max_tokens, stream=True):
            text = chunk.get("choices", [{}])[0].get("text", "")
            yield text

    return StreamingResponse(generate_stream(), media_type="text/plain")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=2268)
