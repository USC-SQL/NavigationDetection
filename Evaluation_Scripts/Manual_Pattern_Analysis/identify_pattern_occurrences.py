import csv
import json
from collections import Counter
from pathlib import Path

DOWNLOADS = Path.home() / "Downloads"
SCRIPT_DIR = Path(__file__).resolve().parent

NAV_ROOT = (
    DOWNLOADS / "RQ1 Results/LLM_Results/Navigation_Output"
)
PRED_ROOT = (
    DOWNLOADS / "RQ1 Results/LLM_Results/LNF_Output"
)
GT_ROOT = (
    DOWNLOADS / "targetted_gt/processed_groundtruth"
)
OUTPUT_DIR = SCRIPT_DIR / "results"

EXPECTED = {"TP": 41, "FP": 14, "FN": 52}

COLUMNS = [
    "subject",
    "classification",
    "transition",
    "source_label",
    "destination_label",
    "source_position",
    "destination_position",
    "source_descriptor",
    "destination_descriptor",
    "observed_in_stage1",
    "browser_boundary",
    "pattern_ids",
    "review_status",
    "review_notes",
]


def normalize(line):
    value = "".join(line.split())
    endpoints = value.split("->")

    if len(endpoints) != 2 or not all(endpoints):
        raise ValueError(f"Invalid transition: {line}")

    return "->".join(endpoints)


def read_edges(path):
    if not path.is_file():
        raise FileNotFoundError(path)

    return {
        normalize(line)
        for line in path.read_text(
            encoding="utf-8-sig"
        ).splitlines()
        if line.strip()
    }


def describe(xpath, elements):
    if xpath == "Browser":
        return {
            "label": "",
            "position": "",
            "descriptor": "Virtual Browser boundary",
        }

    element = elements.get(xpath, {})

    return {
        "label": element.get("label", ""),
        "position": element.get("position", ""),
        "descriptor": element.get("descriptor", ""),
    }


def process_subject(subject):
    nav_path = NAV_ROOT / subject / f"{subject}.json"
    pred_path = PRED_ROOT / subject / f"{subject}.txt"
    gt_path = GT_ROOT / subject / "xpath_failures.txt"

    with nav_path.open(encoding="utf-8-sig") as f:
        navigation = json.load(f)["navigation_order"]

    by_xpath = {e["xpath"]: e for e in navigation}
    by_label = {e["label"]: e for e in navigation}

    # Actual consecutive webpage transitions represented
    # in the Stage 1 navigation data.
    implemented = set()

    for label in sorted(by_label):
        if label + 1 in by_label:
            source = by_label[label]["xpath"]
            target = by_label[label + 1]["xpath"]
            implemented.add(f"{source}->{target}")

    # Browser entry is a modeled candidate, not a
    # verified real-world browser focus event.
    if 1 in by_label:
        implemented.add(
            f"Browser->{by_label[1]['xpath']}"
        )

    predicted = read_edges(pred_path)
    ground_truth = read_edges(gt_path)

    # Include ground-truth and predicted transitions even
    # when they are absent from the Stage 1 candidate set.
    all_edges = implemented | predicted | ground_truth

    rows = []

    for edge in sorted(all_edges):
        is_predicted = edge in predicted
        is_ground_truth = edge in ground_truth

        if is_predicted and is_ground_truth:
            classification = "TP"
        elif is_predicted:
            classification = "FP"
        elif is_ground_truth:
            classification = "FN"
        else:
            classification = "TN"

        source_xpath, target_xpath = edge.split("->")

        source = describe(source_xpath, by_xpath)
        target = describe(target_xpath, by_xpath)

        rows.append({
            "subject": subject,
            "classification": classification,
            "transition": (
                f"{source_xpath} -> {target_xpath}"
            ),
            "source_label": source["label"],
            "destination_label": target["label"],
            "source_position": source["position"],
            "destination_position": target["position"],
            "source_descriptor": source["descriptor"],
            "destination_descriptor": target["descriptor"],
            "observed_in_stage1": edge in implemented,
            "browser_boundary": (
                source_xpath == "Browser"
                or target_xpath == "Browser"
            ),
            "pattern_ids": "",
            "review_status": "Unreviewed",
            "review_notes": "",
        })

    return rows


def main():
    subjects = sorted(
        p.name
        for p in NAV_ROOT.iterdir()
        if p.is_dir()
        and (p / f"{p.name}.json").is_file()
    )

    rows = []
    for subject in subjects:
        rows.extend(process_subject(subject))

    counts = Counter(
        row["classification"] for row in rows
    )

    print("Subjects:", len(subjects))
    for category in ("TP", "FP", "FN", "TN"):
        print(f"{category}: {counts[category]}")

    if len(subjects) != 37:
        raise ValueError(
            f"Expected 37 subjects, found {len(subjects)}"
        )

    for category, expected in EXPECTED.items():
        if counts[category] != expected:
            raise ValueError(
                f"{category}: expected {expected}, "
                f"found {counts[category]}"
            )

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    output_file = OUTPUT_DIR / "transition_inventory.csv"

    if output_file.exists():
        raise FileExistsError(
            f"Output already exists: {output_file}. "
            "Preserve any manual annotations before rerunning."
        )

    with output_file.open(
        "w", newline="", encoding="utf-8-sig"
    ) as f:
        writer = csv.DictWriter(f, fieldnames=COLUMNS)
        writer.writeheader()
        writer.writerows(rows)

    print("\nInventory successfully generated.")
    print("Total transitions:", len(rows))
    print("Saved to:", output_file)


if __name__ == "__main__":
    main()