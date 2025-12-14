import subprocess
from pathlib import Path
from pdf2image import convert_from_path
import shutil
import uuid

# ==============================
# 配置区
# ==============================
SOFFICE = r"C:\Users\Ravon\Downloads\Programs\LibreOfficePortablePrevious\App\libreoffice\program\soffice.exe"
POPPLER_PATH = r"C:\Users\Ravon\Code\Insightful-Verse\src\main\resources\static\bin\poppler-25.12.0\Library\bin"  # 改成你的路径
DPI = 300

SUPPORTED_EXT = {".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx"}

# ==============================
# 主函数
# ==============================
def document_to_images(input_file: str, output_dir: str):
    input_path = Path(input_file)
    if not input_path.exists():
        raise FileNotFoundError(input_file)

    if input_path.suffix.lower() not in SUPPORTED_EXT:
        raise ValueError("Unsupported file type")

    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    work_dir = output_dir / f"_work_{uuid.uuid4().hex}"
    profile_dir = work_dir / "profile"
    work_dir.mkdir()
    profile_dir.mkdir()

    try:
        # 1️⃣ 转 PDF（如果本身不是 PDF）
        if input_path.suffix.lower() != ".pdf":
            subprocess.run([
                SOFFICE,
                "--headless",
                "--nologo",
                "--nolockcheck",
                "--nodefault",
                "--nofirststartwizard",
                f"--env:UserInstallation=file:///{profile_dir.as_posix()}",
                "--convert-to", "pdf",
                "--outdir", str(work_dir),
                str(input_path)
            ], check=True)

            pdf_path = work_dir / (input_path.stem + ".pdf")
        else:
            pdf_path = input_path

        if not pdf_path.exists():
            raise RuntimeError("PDF conversion failed")

        # 2️⃣ PDF → JPG
        pages = convert_from_path(
            pdf_path,
            dpi=DPI,
            poppler_path=POPPLER_PATH
        )

        images = []
        for i, page in enumerate(pages, start=1):
            img_path = output_dir / f"page_{i}.jpg"
            page.save(img_path, "JPEG", quality=95)
            images.append(str(img_path))

        return images

    finally:
        shutil.rmtree(work_dir, ignore_errors=True)

# ==============================
# CLI 调用示例
# ==============================
if __name__ == "__main__":
    imgs = document_to_images("laptop_detail_data_250406.xlsx", "output")
    print("Generated images:")
    for img in imgs:
        print(img)
