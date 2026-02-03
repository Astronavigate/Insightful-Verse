from llama_cpp import Llama
import os

MODEL_PATH = r"models\lmstudio-community\Phi-4-mini-reasoning-GGUF\Phi-4-mini-reasoning-Q4_K_M.gguf"

assert os.path.isfile(MODEL_PATH), f"Model not found: {MODEL_PATH}"

print("[*] Loading model...")

llm = Llama(
    model_path=MODEL_PATH,
    n_ctx=2048,
    n_gpu_layers=0,        # 🔒 强制 CPU（关键）
    n_threads=8,
    verbose=True
)

print("[*] Generating...")

result = llm.create_completion(
    prompt="User: Say Hello\nAssistant:",
    max_tokens=10,
    temperature=0.0,
    stop=["User:"]
)

print("Model output:")
print(result["choices"][0]["text"])
