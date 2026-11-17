import csv
import re
from collections import Counter
from pathlib import Path

BASE = Path(__file__).resolve().parent
INPUT = BASE / "results/transition_inventory.csv"
OUTPUT = BASE / "results/form_control_candidates.csv"

# These keywords identify possible form-related elements.
# A match does not prove that a transition exhibits FN02.
FORM_TERMS = [
    "input", "password", "email", "username",
    "login", "log in", "sign in", "sign up",
    "register", "submit", "checkbox", "radio",
    "forgot", "reset", "search", "form",
    "dropdown", "select", "button", "continue",
    "remember me", "verification", "confirm",
    "account"
]

pattern = re.compile(
    r"\b(?:" + "|".join(
        re.escape(term) for term in FORM_TERMS
    ) + r")\b",
    re.IGNORECASE
)

with INPUT.open(encoding="utf-8-sig", newline="") as f:
    rows = list(csv.DictReader(f))

candidates = []

for row in rows:
    # Browser boundaries are reviewed under FN01.
    if row["browser_boundary"].lower() == "true":
        continue

    source = row["source_descriptor"]
    destination = row["destination_descriptor"]

    source_matches = sorted(
        set(pattern.findall(source)),
        key=str.lower
    )
    destination_matches = sorted(
        set(pattern.findall(destination)),
        key=str.lower
    )

    # Include transitions when at least one endpoint
    # appears form-related, for human review.
    if not source_matches and not destination_matches:
        continue

    candidate = dict(row)
    candidate["source_form_terms"] = ", ".join(source_matches)
    candidate["destination_form_terms"] = ", ".join(
        destination_matches
    )
    candidate["manual_fn02_match"] = ""
    candidate["manual_review_notes"] = ""
    candidates.append(candidate)

OUTPUT.parent.mkdir(parents=True, exist_ok=True)

if OUTPUT.exists():
    raise FileExistsError(
        f"{OUTPUT} already exists. "
        "Preserve existing review annotations."
    )

columns = list(rows[0]) + [
    "source_form_terms",
    "destination_form_terms",
    "manual_fn02_match",
    "manual_review_notes",
]

with OUTPUT.open("w", encoding="utf-8-sig", newline="") as f:
    writer = csv.DictWriter(f, fieldnames=columns)
    writer.writeheader()
    writer.writerows(candidates)

counts = Counter(
    row["classification"] for row in candidates
)

print("Candidate form-related transitions:", len(candidates))
for category in ("TP", "FP", "FN", "TN"):
    print(f"{category}: {counts[category]}")
print("Saved to:", OUTPUT)