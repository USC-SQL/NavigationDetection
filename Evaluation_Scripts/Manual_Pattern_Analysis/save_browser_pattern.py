import csv
from pathlib import Path

BASE = Path(__file__).resolve().parent
INPUT = BASE / "results/transition_inventory.csv"
OUTPUT = BASE / "results/patterns/browser_boundary"

with INPUT.open(encoding="utf-8-sig", newline="") as f:
    rows = list(csv.DictReader(f))

browser_rows = [
    row for row in rows
    if row["browser_boundary"].lower() == "true"
]

for row in browser_rows:
    subject = row["subject"]
    folder = OUTPUT / subject
    folder.mkdir(parents=True, exist_ok=True)

    output_path = folder / "matching_sequences.txt"

    with output_path.open("w", encoding="utf-8") as f:
        f.write("Pattern: FN01 - Browser-boundary omission\n")
        f.write(f"Subject: {subject}\n")
        f.write(f"Classification: {row['classification']}\n")
        f.write(f"Transition: {row['transition']}\n")

print(f"Saved {len(browser_rows)} Browser-boundary records.")
print(f"Output directory: {OUTPUT}")