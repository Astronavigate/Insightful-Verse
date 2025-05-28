import struct

def patch_gguf_metadata(input_path, output_path, new_metadata):
    with open(input_path, "rb") as f:
        data = bytearray(f.read())

    if data[:4] != b"GGUF":
        raise ValueError("不是合法的 GGUF 文件")

    version = struct.unpack("<I", data[4:8])[0]
    if version < 3:
        raise ValueError("仅支持 GGUF V3 或更新版本")

    n_kv = struct.unpack("<Q", data[8:16])[0]
    print(f"原始元数据键值对数量: {n_kv}")

    cursor = 16
    new_data = data[:cursor]

    for i in range(n_kv):
        try:
            key_len = struct.unpack("<Q", data[cursor:cursor + 8])[0]
            cursor += 8

            # 安全范围检查
            if key_len > 256 or cursor + key_len > len(data):
                raise ValueError(f"元数据第 {i} 项 key 长度异常: {key_len}")

            key_bytes = data[cursor:cursor + key_len]
            try:
                key = key_bytes.decode("utf-8")
            except UnicodeDecodeError as e:
                raise UnicodeDecodeError(f"无法解码第 {i} 项的 key，原始字节为: {key_bytes.hex()}，错误: {e}")

            cursor += key_len

            val_type = struct.unpack("<I", data[cursor:cursor + 4])[0]
            cursor += 4

            # 处理 string 类型
            if val_type == 0:
                val_len = struct.unpack("<Q", data[cursor:cursor + 8])[0]
                cursor += 8
                val = data[cursor:cursor + val_len].decode("utf-8")
                cursor += val_len

                if key in new_metadata:
                    new_val = new_metadata[key]
                    print(f"修改 {key}: \"{val}\" → \"{new_val}\"")
                    new_data += struct.pack("<Q", len(key)) + key.encode("utf-8")
                    new_data += struct.pack("<I", 0)
                    new_data += struct.pack("<Q", len(new_val)) + new_val.encode("utf-8")
                else:
                    new_data += struct.pack("<Q", len(key)) + key.encode("utf-8")
                    new_data += struct.pack("<I", 0)
                    new_data += struct.pack("<Q", val_len) + val.encode("utf-8")

            else:
                # 跳过并保留
                start = cursor - 4 - 8 - key_len
                if val_type == 1:
                    cursor += 1
                elif val_type == 2:
                    cursor += 1
                elif val_type == 3:
                    cursor += 2
                elif val_type == 4:
                    cursor += 4
                elif val_type == 5:
                    cursor += 8
                elif val_type == 6:
                    cursor += 4
                elif val_type == 7:
                    cursor += 8
                elif val_type == 8:
                    cursor += 4
                elif val_type == 10:
                    bin_len = struct.unpack("<Q", data[cursor:cursor + 8])[0]
                    cursor += 8 + bin_len
                else:
                    raise NotImplementedError(f"未知 val_type: {val_type}，key = {key}")

                new_data += data[start:cursor]

        except Exception as e:
            print(f"\n❌ 在处理第 {i + 1}/{n_kv} 个元数据项时出错：\n{e}")
            print(f"当前偏移量 cursor = {cursor}")
            break

    # 剩余内容（tensor）直接追加
    new_data += data[cursor:]

    with open(output_path, "wb") as f:
        f.write(new_data)

    print(f"✅ 成功保存至：{output_path}")

# 示例调用
if __name__ == "__main__":
    patch_gguf_metadata(
        input_path="model/Astronav-R1-Distill-Llama-8B-Q8_0.gguf",
        output_path="model/Astronav-R1-Patched.gguf",
        new_metadata={
            "general.name": "Astronav R1",
            "general.basename": "Astronav-R1-Distill-Llama-8B",
            "general.author": "Ravon Technology, Inc."  # 如果原来没有这个 key，会忽略
        }
    )
