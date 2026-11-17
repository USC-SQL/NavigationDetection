import subprocess
from pathlib import Path

BASE = Path(
    "Evaluation_Scripts/LNF_Error_Pattern_Discovery"
)

INPUT_DIR = BASE / "subject_inputs"
OUTPUT_DIR = BASE / "subject_analysis"

CLASSPATH_FILE = Path("target/lnf-classpath.txt")

if not CLASSPATH_FILE.is_file():
    raise FileNotFoundError(
        "Missing Maven dependency classpath. "
        "Run mvn dependency:build-classpath first."
    )

classpath = (
    "target/classes:"
    + CLASSPATH_FILE.read_text().strip()
)

subjects = sorted(
    path.stem for path in INPUT_DIR.glob("*.json")
)

successful = []
skipped = []
failed = []

print(f"Total subjects: {len(subjects)}")

for index, subject in enumerate(subjects, 1):

    output_path = OUTPUT_DIR / f"{subject}.json"

    if output_path.exists():
        print(f"[{index}/{len(subjects)}] SKIP: {subject}")
        skipped.append(subject)
        continue

    print(
        f"\n[{index}/{len(subjects)}] ANALYZING: {subject}",
        flush=True
    )

    command = [
        "java",
        "-cp",
        classpath,
        "edu.usc.LLM.LNFErrorPatternDiscoveryAgent",
        subject,
    ]

    result = subprocess.run(command, check=False)

    if result.returncode == 0 and output_path.exists():
        successful.append(subject)
        print(f"SUCCESS: {subject}")
    else:
        failed.append(subject)
        print(f"FAILED: {subject}")

print("\n" + "=" * 50)
print("BATCH SUMMARY")
print("=" * 50)

print("Newly successful:", len(successful))
print("Previously completed:", len(skipped))
print("Failed:", len(failed))

print("\nFailed subjects:")
for subject in failed:
    print(" -", subject)

print("\nTotal saved subject outputs:",
      len(list(OUTPUT_DIR.glob("*.json"))))