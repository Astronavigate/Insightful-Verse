from llama_cpp import Llama
import os

MODEL_PATH = "model/DeepSeek-R1-Distill-Llama-8B-Q8_0.gguf"

if not os.path.exists(MODEL_PATH):
    print(f"Error: Model file not found at {MODEL_PATH}")
    print("Please check the path or download the model.")
    exit(1) # Exit if model is not found

try:
    print(f"Loading model from {MODEL_PATH}...")
    llm = Llama(
        model_path=MODEL_PATH,
        n_ctx=2048,
        n_gpu_layers=-1, # Try -1 for all layers on GPU
        n_threads=8,
        verbose=True # Enable verbose output from llama.cpp
    )
    print("Model loaded successfully!")

    print("\n--- Testing simple completion (non-streaming) ---")
    output = llm.create_completion(
        prompt="你好，你叫什么？可以干什么？",
        max_tokens=50,
        stream=False,
        temperature=0.7 # Add temperature to ensure randomness if needed
    )
    print("Non-streaming output:")
    print(output["choices"][0]["text"])

    print("\n--- Testing streaming completion ---")
    stream_output = ""
    for chunk in llm.create_completion(
        prompt="你好，你叫什么？可以干什么？",
        max_tokens=50,
        stream=True,
        temperature=0.7
    ):
        text_chunk = chunk.get("choices", [{}])[0].get("text", "")
        print(f"Received chunk: '{text_chunk}'") # See what chunks are received
        stream_output += text_chunk

    print("\nStreaming output (full):")
    print(stream_output)

except Exception as e:
    print(f"\nAn error occurred during model loading or generation: {e}")
    # If this also produces SIGSEGV, it's a deeper issue.
    # You might want to run this in a debugger if you're familiar with gdb/lldb.