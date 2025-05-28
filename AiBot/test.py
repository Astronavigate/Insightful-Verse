import requests
import random
import time

import torch

SERVER_URL = "http://127.0.0.1:2268"
USE_STREAM = True  # 修改为 False 走非流式 /generate

print(torch.cuda.is_available())

example_prompts = [
    "请解释量子纠缠的基本原理。",
    "你对人工智能未来发展的看法是什么？",
    "写一首关于孤独与坚韧的现代诗。",
    "用中文介绍一下 Llama 模型的结构。",
    "给出一个使用 Python 实现快排的例子。",
    "介绍一下 Gigabyte。",
    "什么是文艺复兴？它对世界产生了什么影响？",
    "请简述庄子的“逍遥游”思想。",
    "What are the AI acceleration capabilities of the AMD Ryzen AI Max+ Pro 395?",
    "How does the Apple M4 Max compare to previous Apple Silicon chips in performance and efficiency?",
    "Explain how a CPU works.",
    "How does a GPU accelerate neural networks?",
    "What is virtualization and how does it relate to QEMU?",
    "Describe the architecture of the LLaMA language model.",
]

for i in range(5):
    prompt = random.choice(example_prompts)
    payload = {
        "prompt": prompt,
        "max_tokens": 131072,
        "stream": USE_STREAM
    }

    print(f"\n📤 第 {i+1} 次请求，Prompt: {prompt}")

    try:
        if USE_STREAM:
            with requests.post(f"{SERVER_URL}/stream_generate", json=payload, stream=True) as r:
                r.raise_for_status()
                print("📥 模型流式输出：", end="", flush=True)
                for chunk in r.iter_content(chunk_size=None):
                    print(chunk.decode("utf-8"), end="", flush=True)
            print()  # 换行
        else:
            response = requests.post(f"{SERVER_URL}/generate", json=payload)
            response.raise_for_status()
            result = response.json()
            print("📥 模型完整回复:", result["result"].strip())

    except Exception as e:
        print("❌ 请求失败:", e)

    time.sleep(1)
