import os
import subprocess
from pathlib import Path

# --- 配置参数 ---
# 设置要搜索和转换的根目录。使用绝对路径以确保精确性。
TARGET_DIRECTORY = r".\Audio"
# 替换为您的实际路径

# 定义所有待转换的源音频格式
SOURCE_FORMATS = ['*.m4a', '*.mp3', '*.wav', '*.aif', '*.aiff', '*.flac']  # FLAC to FLAC 也可以用于元数据统一或压缩率调整

# FFmpeg 命令行配置
FFMPEG_EXE = 'ffmpeg'

# 核心选项：
# -map_metadata 0: 确保元数据从输入文件映射到输出文件。
# -c:a flac: 目标编码器为 FLAC。
# -compression_level 8: FLAC的压缩级别（0-12），8是默认且平衡的设置，体现高效。
FFMPEG_OPTIONS = ['-map_metadata', '0', '-c:a', 'flac', '-compression_level', '8']


# --- 核心处理函数 ---
def convert_all_to_flac(source_dir: str):
    """
    递归遍历指定目录，将所有已知的源音频格式转换为 FLAC 格式。
    """
    print(f"--- 启动 Raven Audio 统一格式转换系统 ---")
    print(f"目标目录: {source_dir}")

    source_path = Path(source_dir)
    if not source_path.is_dir():
        print(f"ERROR: 目录不存在或路径错误。请校验 TARGET_DIRECTORY。")
        return

    # 1. 查找所有目标文件
    all_files_to_convert = []
    for pattern in SOURCE_FORMATS:
        # 使用 rglob 递归搜索所有子目录
        found_files = list(source_path.rglob(pattern))
        # 排除掉已经是 .flac 且不需要重复处理的文件 (如果 SOURCE_FORMATS 中包含 *.flac)
        if pattern == '*.flac':
            # 如果需要对 FLAC 文件重新编码或统一元数据，则保留；否则可以跳过
            continue

        all_files_to_convert.extend(found_files)

    if not all_files_to_convert:
        print("STATUS: 未找到任何待转换的源音频文件。任务序列完成。")
        return

    print(f"找到 {len(all_files_to_convert)} 个不同格式的源文件待处理...")

    for index, source_file in enumerate(all_files_to_convert):
        # 构造输出路径：保持原路径结构和文件名，更改扩展名
        output_file = source_file.with_suffix('.flac')

        # 避免在同一目录覆盖原文件，建议输出到新目录以保证安全和秩序
        # 如果需要输出到原目录，请确保原文件已备份。

        # 为了安全，这里将输出文件放在 "Converted_FLAC" 子目录下
        relative_path = source_file.relative_to(source_path.parent)
        output_path_safe = source_path.parent / "Converted_FLAC" / relative_path.with_suffix('.flac')

        # 确保输出目录存在
        output_path_safe.parent.mkdir(parents=True, exist_ok=True)

        # 构造完整的 FFmpeg 命令
        command = [
            FFMPEG_EXE,
            '-i', str(source_file),  # 输入文件
            *FFMPEG_OPTIONS,  # 转换选项
            str(output_path_safe)  # 输出文件
        ]

        # 状态反馈：体现克制与精确
        print(f"\n[{index + 1}/{len(all_files_to_convert)}] SOURCE: {source_file.relative_to(source_path.parent)}")
        print(f"        TARGET: {output_path_safe.relative_to(source_path.parent)}")

        try:
            subprocess.run(
                command,
                capture_output=True,
                text=True,
                check=True,
                encoding='utf-8',
                timeout=300  # 设置超时时间，防止进程卡死 (例如 300 秒)
            )
            print("        RESULT: 转换成功 (CODE: 0)")

        except subprocess.CalledProcessError as e:
            # 捕获 FFmpeg 转换错误，提供洞察
            print(f"        FAILURE: FFmpeg 返回非零代码 {e.returncode}")
            print("        DEBUG (Error Log Snippet):")
            # 筛选关键错误信息
            for line in e.stderr.splitlines():
                if 'Error' in line or 'Failed' in line or 'Unknown' in line or 'Invalid' in line:
                    print(f"          > {line}")

        except FileNotFoundError:
            print("CRITICAL ERROR: FFmpeg 可执行文件未找到。请检查环境变量配置！")
            return
        except subprocess.TimeoutExpired:
            print("CRITICAL ERROR: FFmpeg 进程超时。请检查源文件是否损坏。")

    print("\n--- 任务序列完成，所有文件已收拢至 Converted_FLAC 目录 ---")


# --- 运行模块 ---
if __name__ == "__main__":
    # 在运行前，请务必修改 TARGET_DIRECTORY
    convert_all_to_flac(TARGET_DIRECTORY)