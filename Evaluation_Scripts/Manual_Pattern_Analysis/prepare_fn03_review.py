import csv
from pathlib import Path

BASE = Path(__file__).resolve().parent
INPUT = BASE / "results/transition_inventory.csv"
OUTPUT = BASE / "results/fn03_review.csv"

with INPUT.open(encoding="utf-8-sig", newline="") as f:
    rows = list(csv.DictReader(f))

# Examine all non-Browser false negatives.
# We will manually determine which belong to FN03.
candidates = [
    row for row in rows
    if row["classification"] == "FN"
    and row["browser_boundary"].lower() == "false"
]

fields = [
    "subject",
    "transition",
    "source_label",
    "destination_label",
    "source_descriptor",
    "destination_descriptor",
    "source_position",
    "destination_position",
    "manual_fn03_match",
    "visual_kfg_verified",
    "review_notes",
]

OUTPUT.parent.mkdir(parents=True, exist_ok=True)

if OUTPUT.exists():
    raise FileExistsError(
        f"{OUTPUT} already exists. "
        "Preserve existing review annotations."
    )

with OUTPUT.open("w", encoding="utf-8-sig", newline="") as f:
    writer = csv.DictWriter(f, fieldnames=fields)
    writer.writeheader()

    for row in candidates:
        writer.writerow({
            **{key: row.get(key, "") for key in fields},
            "manual_fn03_match": "",
            "visual_kfg_verified": "No",
            "review_notes": "",
        })

print("FN03 review candidates:", len(candidates))
print("Saved to:", OUTPUT)

print("\nSubjects with non-Browser false negatives:")
for subject in sorted(set(row["subject"] for row in candidates)):
    count = sum(
        row["subject"] == subject for row in candidates
    )
    print(f"  {subject}: {count}")