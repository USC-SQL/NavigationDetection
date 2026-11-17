import csv
from pathlib import Path

BASE = Path(__file__).resolve().parent
INPUT = BASE / "results/form_control_candidates.csv"
OUTPUT = BASE / "results/fn02_review.csv"

with INPUT.open(encoding="utf-8-sig", newline="") as f:
    rows = list(csv.DictReader(f))

false_negatives = [
    row for row in rows
    if row["classification"] == "FN"
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
    "manual_fn02_match",
    "manual_review_notes",
]

OUTPUT.parent.mkdir(parents=True, exist_ok=True)

if OUTPUT.exists():
    raise FileExistsError(
        f"{OUTPUT} already exists. "
        "Preserve any existing annotations."
    )

with OUTPUT.open("w", encoding="utf-8-sig", newline="") as f:
    writer = csv.DictWriter(
        f, fieldnames=fields, extrasaction="ignore"
    )
    writer.writeheader()
    writer.writerows(false_negatives)

print("FN02 candidates requiring review:", len(false_negatives))
print("Saved to:", OUTPUT)