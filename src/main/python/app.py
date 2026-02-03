import asyncio
import json
import uuid

import uvicorn
from fastapi import FastAPI, HTTPException
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
from typing import Optional, Dict, Any

import service
from service import WeaviateService, LLMService, WhisperService

app = FastAPI(title="Ravon Unified AI Gateway")

# =========================
# 单例初始化
# =========================
vector_service = WeaviateService()
model_service = LLMService(vector_service)
whisper_service = WhisperService()

# 存储打断事件，key=session_id -> asyncio.Event
interrupt_flags: Dict[str, asyncio.Event] = {}

# =========================
# Request Models
# =========================

class UpdVectorRequest(BaseModel):
    table_id: str
    table_name: str
    content: str
    metadata: Dict[str, Any] = {}

class DelVectorRequest(BaseModel):
    table_id: str
    table_name: str

class QueryVectorRequest(BaseModel):
    query: str
    table_name: Optional[str] = None
    top_k: int = 3

class QueryByIdRequest(BaseModel):
    table_id: str
    table_name: str

class AskRequest(BaseModel):
    query: str
    table_name: Optional[str] = None

class TranscribeRequest(BaseModel):
    audio_path: str

# =========================
# Routes
# =========================

@app.get("/status")
async def status():
    return {
        "status": "online",
        "hardware": service.hw_config["desc"]
    }

@app.post("/updVector")
async def update(req: UpdVectorRequest):
    try:
        uid = vector_service.upsert_vector(
            req.table_id,
            req.table_name,
            req.content,
            req.metadata
        )
        return {"status": "success", "uuid": uid}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/delVector")
async def delete(req: DelVectorRequest):
    uid = vector_service.delete_vector(req.table_id, req.table_name)
    return {"status": "deleted", "uuid": uid}

@app.post("/queryVector")
async def query(req: QueryVectorRequest):
    docs = vector_service.search(
        req.query,
        req.table_name,
        req.top_k
    )
    return [
        {"content": d.page_content, "metadata": d.metadata}
        for d in docs
    ]

@app.post("/queryById")
async def get_by_id(req: QueryByIdRequest):
    obj, uid = vector_service.get_object(req.table_id, req.table_name)
    if not obj:
        raise HTTPException(status_code=404, detail="Vector Not Found")
    return {
        "uuid": uid,
        "content": obj.properties.get("content"),
        "metadata": obj.properties.get("metadata")
    }

@app.post("/ask")
async def chat(req: AskRequest):
    return model_service.think_and_answer(
        req.query,
        req.table_name
    )

@app.post("/ask/stream")
async def ask_stream(req: AskRequest):
    # 生成唯一 session ID
    session_id = str(uuid.uuid4())
    stop_event = asyncio.Event()
    interrupt_flags[session_id] = stop_event

    return StreamingResponse(
        stream_answer_async(req.query, req.table_name, stop_event, session_id),
        media_type="text/event-stream",
        headers={"X-Session-ID": session_id}
    )

@app.post("/ask/interrupt/{session_id}")
async def interrupt_stream(session_id: str):
    stop_event = interrupt_flags.get(session_id)
    if stop_event:
        stop_event.set()
        return {"status": "interrupted", "session_id": session_id}
    else:
        raise HTTPException(status_code=404, detail="Session not found")

@app.post("/transcribe")
async def whisper_transcribe(req: TranscribeRequest):
    try:
        srt_path = whisper_service.transcribe_to_srt(req.audio_path)
        return {
            "status": "success",
            "audio": req.audio_path,
            "srt": srt_path
        }
    except FileNotFoundError:
        raise HTTPException(status_code=404, detail="Audio file not found")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# =========================
# SSE 流式生成逻辑（支持打断）
# =========================
async def stream_answer_async(query: str, table_name: str = None, stop_event: Optional[asyncio.Event] = None, session_id: Optional[str] = None):
    prompt, docs = model_service._build_prompt(query, table_name)
    q: asyncio.Queue = asyncio.Queue()

    def producer():
        try:
            llm = model_service._new_llm()
            q.put_nowait(("metadata", [d.metadata for d in docs]))

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
                token = chunk["choices"][0].get("text", "")
                if token:
                    q.put_nowait(("token", token))
                if stop_event and stop_event.is_set():
                    q.put_nowait(("error", "Generation interrupted by user"))
                    break

            q.put_nowait((None, None))
        except Exception as e:
            q.put_nowait(("error", str(e)))
            q.put_nowait((None, None))

    loop = asyncio.get_running_loop()
    loop.run_in_executor(None, producer)

    while True:
        typ, payload = await q.get()
        if typ is None:
            break
        if typ == "metadata":
            yield f"data: {json.dumps({'type': 'metadata', 'sources': payload}, ensure_ascii=False)}\n\n"
        elif typ == "token":
            yield f"data: {json.dumps({'type': 'token', 'content': payload}, ensure_ascii=False)}\n\n"
        elif typ == "error":
            yield f"data: {json.dumps({'type': 'error', 'message': payload}, ensure_ascii=False)}\n\n"
        await asyncio.sleep(0)

    # 流结束，清理 session
    if session_id:
        interrupt_flags.pop(session_id, None)

# =========================
# Entry
# =========================
if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=7824)