import csv
from pathlib import Path
from collections import Counter

BASE = Path(__file__).resolve().parent
INPUT = BASE / "results/fn02_review_annotated.csv"
OUTPUT = BASE / "results/fn02_uncertain.csv"

with INPUT.open(encoding="utf-8-sig", newline="") as f:
    reader = csv.DictReader(f)
    fields = reader.fieldnames
    rows = list(reader)

print("Assessment breakdown:")
counts = Counter(row["provisional_assessment"] for row in rows)

for assessment, count in sorted(counts.items()):
    print(f"  {assessment}: {count}")

uncertain = [
    row for row in rows
    if row["provisional_assessment"].strip().lower()
    == "needs visual/kfg review"
]

if OUTPUT.exists():
    raise FileExistsError(
        f"{OUTPUT} already exists. Preserve existing annotations."
    )
uncertain = [
    row for row in rows
    if row["provisional_assessment"].strip().lower()
    == "needs visual/kfg review"
]
with OUTPUT.open("w", encoding="utf-8-sig", newline="") as f:
    writer = csv.DictWriter(f, fieldnames=fields)
    writer.writeheader()
    writer.writerows(uncertain)

print("\nUncertain transitions:", len(uncertain))
print("Saved to:", OUTPUT)

print("\nSubjects requiring verification:")
for row in uncertain:
    print(
        f"  {row['subject']}: "
        f"{row['source_descriptor']} -> "
        f"{row['destination_descriptor']}"
    )