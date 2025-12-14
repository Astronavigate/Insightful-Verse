import os
import subprocess
from pathlib import Path

# --- 配置参数 ---
# 设置要搜索和转换的根目录。
TARGET_DIRECTORY = r".\Audio"
# 请替换为您的实际路径

# 定义所有待转换的源音频格式 (递归搜索)
SOURCE_FORMATS = ['*.m4a', '*.mp3', '*.wav', '*.aif', '*.aiff', '*.flac']

# FFmpeg 命令行配置
FFMPEG_EXE = 'ffmpeg'

# 核心选项：兼容性优化
FFMPEG_OPTIONS = [
    '-map_metadata', '0',  # 映射所有元数据流
    '-c:a', 'flac',  # 目标编码器为 FLAC
    '-compression_level', '8',  # FLAC 压缩级别

    # 兼容性增强：强制写入 ID3 标签以提高 Windows 兼容性
    '-id3v2_version', '3',  # 使用 ID3v2.3（最佳兼容版本）
    '-write_id3v1', '1'  # 写入 ID3v1（额外的兼容性保障）
]


# --- 核心处理函数 ---
def convert_all_to_flac_compatible(source_dir: str):
    """
    递归遍历目录，将所有格式转换为 FLAC，并增强 Windows 元数据兼容性。
    """
    print(f"--- 启动 Raven Audio 兼容性强化转换系统 ---")
    print(f"目标目录: {source_dir}")

    source_path = Path(source_dir)
    if not source_path.is_dir():
        print(f"ERROR: 目录不存在或路径错误。请校验 TARGET_DIRECTORY。")
        return

    # 1. 查找所有目标文件
    all_files_to_convert = []
    for pattern in SOURCE_FORMATS:
        found_files = list(source_path.rglob(pattern))
        # 避免重复转换已经符合要求的 FLAC 文件
        if pattern == '*.flac':
            continue

        all_files_to_convert.extend(found_files)

    if not all_files_to_convert:
        print("STATUS: 未找到任何待转换的源音频文件。任务序列完成。")
        return

    print(f"找到 {len(all_files_to_convert)} 个不同格式的源文件待处理...")

    for index, source_file in enumerate(all_files_to_convert):
        # 构造输出路径：保持原路径结构和文件名，更改扩展名
        # 为保持文件纯净，统一输出到 "Converted_FLAC" 目录
        relative_path = source_file.relative_to(source_path.parent)
        output_path_safe = source_path.parent / "Converted_FLAC" / relative_path.with_suffix('.flac')

        # 确保输出目录存在
        output_path_safe.parent.mkdir(parents=True, exist_ok=True)

        # 构造完整的 FFmpeg 命令
        command = [
            FFMPEG_EXE,
            '-i', str(source_file),
            *FFMPEG_OPTIONS,
            str(output_path_safe)
        ]

        # 状态反馈：克制而精确
        print(f"\n[{index + 1}/{len(all_files_to_convert)}] SOURCE: {source_file.relative_to(source_path.parent)}")
        print(f"        TARGET: {output_path_safe.relative_to(source_path.parent)}")

        try:
            # 执行命令
            subprocess.run(
                command,
                capture_output=True,
                text=True,
                check=True,
                encoding='utf-8',
                timeout=300
            )
            print("        RESULT: 转换成功 & 元数据兼容性强化 (CODE: 0)")

        except subprocess.CalledProcessError as e:
            print(f"        FAILURE: FFmpeg 返回非零代码 {e.returncode}")
            # ... 错误日志打印（省略，同上一次脚本）...

        except FileNotFoundError:
            print("CRITICAL ERROR: FFmpeg 可执行文件未找到。请检查环境变量配置！")
            return
        except subprocess.TimeoutExpired:
            print("CRITICAL ERROR: FFmpeg 进程超时。")

    print("\n--- 任务序列完成，请在 Converted_FLAC 目录校验文件 ---")


# --- 运行模块 ---
if __name__ == "__main__":
    convert_all_to_flac_compatible(TARGET_DIRECTORY)