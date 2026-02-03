import asyncio
import json
import uuid
import uvicorn
from fastapi import FastAPI, HTTPException
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
from typing import Optional, Dict, Any

import service
from min_service import WeaviateService, LLMService, WhisperService

app = FastAPI(title="Ravon Unified AI Gateway")

# 单例初始化
vector_service = WeaviateService()
model_service = LLMService(vector_service)
whisper_service = WhisperService()

interrupt_flags: Dict[str, asyncio.Event] = {}

# --- 模型定义 ---
class UpdVectorRequest(BaseModel):
    table_id: str; table_name: str; content: str;

class DelVectorRequest(BaseModel):
    table_id: str; table_name: str

class QueryVectorRequest(BaseModel):
    query: str; table_name: Optional[str] = None; top_k: int = 3

class AskRequest(BaseModel):
    query: str; table_name: Optional[str] = None

class TranscribeRequest(BaseModel):
    audio_path: str

# --- 向量接口 ---
@app.post("/updVector")
async def update_vector(req: UpdVectorRequest):
    uid = vector_service.upsert_vector(req.table_id, req.table_name, req.content)
    return {"status": "success", "uuid": uid}

@app.post("/delVector")
async def delete_vector(req: DelVectorRequest):
    uid = vector_service.delete_vector(req.table_id, req.table_name)
    return {"status": "deleted", "uuid": uid}

@app.post("/queryVector")
async def query_vector(req: QueryVectorRequest):
    docs = vector_service.search(req.query, req.table_name, req.top_k)
    return [{"content": d.page_content, "metadata": d.metadata} for d in docs]

# --- 问答接口 ---
@app.post("/ask")
async def chat(req: AskRequest):
    return model_service.think_and_answer(req.query, req.table_name)

@app.post("/ask/stream")
async def ask_stream(req: AskRequest):
    session_id = str(uuid.uuid4())
    stop_event = asyncio.Event()
    interrupt_flags[session_id] = stop_event
    gen_func = model_service.stream_answer(req.query, req.table_name)

    async def event_generator():
        try:
            for chunk in gen_func():
                if stop_event.is_set():
                    yield f"data: {json.dumps({'type': 'error', 'message': 'Interrupted'})}\n\n"
                    break
                yield f"data: {chunk}\n\n"
                await asyncio.sleep(0.01)
        finally:
            interrupt_flags.pop(session_id, None)

    return StreamingResponse(event_generator(), media_type="text/event-stream", headers={"X-Session-ID": session_id})

# --- 字幕接口 ---
@app.post("/transcribe")
async def whisper_transcribe(req: TranscribeRequest):
    try:
        srt_path = whisper_service.transcribe_to_srt(req.audio_path)
        return {"status": "success", "srt": srt_path}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/ask/interrupt/{session_id}")
async def interrupt_stream(session_id: str):
    if event := interrupt_flags.get(session_id):
        event.set()
        return {"status": "interrupted"}
    raise HTTPException(status_code=404, detail="Session not found")

@app.get("/status")
async def status():
    return {"status": "online", "hardware": service.hw_config["desc"]}

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=7824)
