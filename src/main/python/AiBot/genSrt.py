"""import whisper

model = whisper.load_model("base")
result = model.transcribe("Nickelback - What Are You Waiting For.m4a")
print(result)
print(type(result))
print(result["text"])"""

import whisper
from whisper.utils import get_writer

# 1. 加载模型与转写
model = whisper.load_model("base")
# audio_path = "C:\\Users\\Ravon\\Music\\大鱼 (唱片版).flac"
audio_path = "./audio/絵本.m4a"
result = model.transcribe(audio_path)

# 2. 配置输出
output_directory = "."  # 当前目录
output_format = "srt"   # 指定格式: "srt", "vtt", "txt", "json", "tsv"

# 3. 实例化写入器并保存
# word_options 用于控制断行逻辑（可选）
word_options = {
    "highlight_words": False,
    "max_line_count": 50,
    "max_line_width": 50
}

writer = get_writer(output_format, output_directory)
writer(result, audio_path, word_options)

print(f"转写完成，已保存为 {audio_path}.srt")
