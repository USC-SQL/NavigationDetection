import json
import subprocess
from pathlib import Path

BASE = Path(
    "Evaluation_Scripts/LNF_Error_Pattern_Discovery"
)

INPUT_DIR = BASE / "matching_inputs"
OUTPUT_DIR = BASE / "matching_outputs"
FAILURE_FILE = BASE / "matching_batch_failures.txt"

CLASSPATH_FILE = Path("target/lnf-classpath.txt")

if not CLASSPATH_FILE.exists():
    raise FileNotFoundError(
        "Missing dependency classpath. "
        "Run mvn dependency:build-classpath first."
    )

classpath = (
    "target/classes:"
    + CLASSPATH_FILE.read_text().strip()
)

subjects = sorted(
    path.stem
    for path in INPUT_DIR.glob("*.json")
)

if len(subjects) != 37:
    raise ValueError(
        f"Expected 37 subjects, found {len(subjects)}"
    )

OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

successful = []
skipped = []
failed = []

for index, subject in enumerate(subjects, 1):

    output_path = OUTPUT_DIR / f"{subject}.json"

    if output_path.exists():
        print(
            f"[{index}/37] SKIPPED: {subject}",
            flush=True
        )
        skipped.append(subject)
        continue

    print(
        f"\n[{index}/37] ANALYZING: {subject}",
        flush=True
    )

    command = [
        "java",
        "-cp",
        classpath,
        "edu.usc.LLM.LNFPatternMatchingAgent",
        subject,
    ]

    try:
        result = subprocess.run(
            command,
            check=False
        )

        if result.returncode == 0 and output_path.exists():
            successful.append(subject)
            print(f"[SUCCESS] {subject}", flush=True)
        else:
            failed.append(subject)
            print(f"[FAILED] {subject}", flush=True)

    except KeyboardInterrupt:
        print("\nInterrupted. Completed outputs are preserved.")
        raise

FAILURE_FILE.write_text(
    "\n".join(failed) + ("\n" if failed else ""),
    encoding="utf-8"
)

total_transitions = 0
total_matches = 0
total_uncertain = 0

for path in sorted(OUTPUT_DIR.glob("*.json")):

    data = json.loads(
        path.read_text(encoding="utf-8")
    )

    assessments = data["transition_assessments"]

    total_transitions += len(assessments)

    for assessment in assessments:
        total_matches += len(assessment["matches"])
        total_uncertain += len(assessment["uncertain"])

print("\n" + "=" * 55)
print("FULL-DATASET MATCHING SUMMARY")
print("=" * 55)

print("Newly successful:", len(successful))
print("Previously completed:", len(skipped))
print("Failed:", len(failed))

print("Total completed subjects:",
      len(list(OUTPUT_DIR.glob("*.json"))))

print("Transitions evaluated:", total_transitions)
print("Pattern matches:", total_matches)
print("Uncertain decisions:", total_uncertain)

print("\nFailed subjects:")
for subject in failed:
    print(" -", subject)

print("\nFailure list:", FAILURE_FILE)