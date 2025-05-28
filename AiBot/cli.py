import requests
import time
import threading

try:
    import customtkinter as ctk
    GUI_AVAILABLE = True
except ImportError:
    GUI_AVAILABLE = False


def stream_chat(ip, prompt, callback):
    try:
        url = f"http://{ip}/stream_generate"
        payload = {
            "prompt": prompt,
            "max_tokens": 131072,
            "stream": True
        }
        with requests.post(url, json=payload, stream=True) as r:
            r.raise_for_status()
            for chunk in r.iter_content(chunk_size=None):
                callback(chunk.decode("utf-8"))
    except Exception as e:
        callback(f"\n❌ 请求失败: {e}")


# =======================
# ✅ GUI 模式（ctk 存在）
# =======================
def run_gui():
    ctk.set_appearance_mode("System")
    ctk.set_default_color_theme("blue")

    app = ctk.CTk()
    app.title("AI 聊天工具")
    app.geometry("720x480")

    ip_var = ctk.StringVar(value="127.0.0.1:2268")
    prompt_var = ctk.StringVar()

    def start_chat():
        ip = ip_var.get().strip()
        prompt = prompt_var.get().strip()
        if not prompt:
            output_box.insert("end", "⚠️ 请输入问题。\n")
            return

        output_box.delete("1.0", "end")

        def stream_callback(chunk):
            output_box.insert("end", chunk)
            output_box.see("end")

        threading.Thread(target=stream_chat, args=(ip, prompt, stream_callback), daemon=True).start()

    ctk.CTkLabel(app, text="服务器 IP:").pack(pady=(10, 0))
    ctk.CTkEntry(app, textvariable=ip_var).pack(fill="x", padx=20)

    ctk.CTkLabel(app, text="输入你的问题:").pack(pady=(10, 0))
    ctk.CTkEntry(app, textvariable=prompt_var).pack(fill="x", padx=20)

    ctk.CTkButton(app, text="发送", command=start_chat).pack(pady=10)

    output_box = ctk.CTkTextbox(app, wrap="word")
    output_box.pack(expand=True, fill="both", padx=20, pady=(0, 10))

    app.mainloop()


# =======================
# 🖥️ CLI 模式（无 ctk）
# =======================
def run_cli():
    print("✅ 进入命令行聊天模式")
    ip = input("请输入服务器 IP（默认127.0.0.1:2268）：").strip() or "127.0.0.1:2268"

    while True:
        prompt = input("\n请输入你的问题：").strip()
        if not prompt:
            print("⚠️ 不能为空！")
            continue

        print("📥 模型流式回复：", end="", flush=True)

        def print_callback(chunk):
            print(chunk, end="", flush=True)

        stream_chat(ip, prompt, print_callback)
        print()

        again = input("\n是否继续？(Y/n): ").strip().lower()
        if again == "n":
            break


# =======================
# ▶️ 入口点
# =======================
if __name__ == "__main__":
    if GUI_AVAILABLE:
        run_gui()
    else:
        run_cli()
