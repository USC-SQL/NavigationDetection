import csv
import json
from collections import Counter, defaultdict
from pathlib import Path

# Repository: ~/Desktop/490/NavigationDetection
REPO = Path(__file__).resolve().parents[1]
DOWNLOADS = Path.home() / "Downloads"

KFG_ROOT = REPO.parent / "LYNX_Results/KFFGs_perfect"
NAV_ROOT = DOWNLOADS / "RQ1 Results/LLM_Results/Navigation_Output"
PRED_ROOT = DOWNLOADS / "RQ1 Results/LLM_Results/LNF_Output"
GT_ROOT = DOWNLOADS / "targetted_gt/processed_groundtruth"
SCREEN_ROOT = DOWNLOADS / "Empirical_Study_Results"

INVENTORY = (
    REPO / "Evaluation_Scripts/Manual_Pattern_Analysis/"
    "results/transition_inventory.csv"
)

OUTPUT_ROOT = (
    REPO / "Evaluation_Scripts/"
    "LNF_Error_Pattern_Discovery/subject_inputs"
)


def normalize_transition(line):
    value = "".join(line.split())
    parts = value.split("->")

    if len(parts) != 2 or not all(parts):
        raise ValueError(f"Invalid transition: {line}")

    return " -> ".join(parts)


def read_transitions(path):
    if not path.is_file():
        raise FileNotFoundError(path)

    return [
        normalize_transition(line)
        for line in path.read_text(
            encoding="utf-8-sig"
        ).splitlines()
        if line.strip()
    ]


def read_json(path):
    if not path.is_file():
        raise FileNotFoundError(path)

    with path.open(encoding="utf-8-sig") as f:
        return json.load(f)


def load_inventory():
    with INVENTORY.open(
        encoding="utf-8-sig", newline=""
    ) as f:
        rows = list(csv.DictReader(f))

    counts = Counter(
        row["classification"] for row in rows
    )

    expected = {"TP": 41, "FP": 14, "FN": 52}

    for category, count in expected.items():
        if counts[category] != count:
            raise ValueError(
                f"Unexpected {category} count: "
                f"{counts[category]} (expected {count})"
            )

    by_subject = defaultdict(list)

    # Exclude all manual-pattern classifications.
    # The new agent must discover patterns independently.
    fields = [
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
    ]

    for row in rows:
        by_subject[row["subject"]].append({
            key: row.get(key, "")
            for key in fields
        })

    return by_subject


def prepare_subject(subject, inventory):
    kfg_path = KFG_ROOT / subject / "KFFG.json"
    nav_path = NAV_ROOT / subject / f"{subject}.json"
    pred_path = PRED_ROOT / subject / f"{subject}.txt"
    gt_path = GT_ROOT / subject / "xpath_failures.txt"
    screenshot_dir = SCREEN_ROOT / subject

    kfg = read_json(kfg_path)
    navigation = read_json(nav_path)
    predictions = read_transitions(pred_path)
    ground_truth = read_transitions(gt_path)

    if not screenshot_dir.is_dir():
        raise FileNotFoundError(screenshot_dir)

    screenshots = sorted(
        p for p in screenshot_dir.iterdir()
        if p.suffix.lower() in {
            ".jpg", ".jpeg", ".png", ".webp"
        }
    )

    if not screenshots:
        raise ValueError(
            f"No screenshots found for {subject}"
        )

    severity = {}

    for name in ("Strongly", "Slightly", "Neutral"):
        path = GT_ROOT / subject / f"{name}.txt"
        if path.is_file():
            severity[name] = read_transitions(path)

    return {
        "subject": subject,
        "evidence_description": {
            "original_predictions":
                "Original Stage 2 detected LNF transitions.",
            "ground_truth":
                "Reference LNF transitions.",
            "navigation_order":
                "Stage 1 inferred meaningful order and "
                "implemented keyboard labels.",
            "kfg":
                "Original keyboard focus graph JSON.",
            "evaluated_transitions":
                "Original prediction-vs-ground-truth "
                "classification inventory. TN labels are "
                "provisional within the modeled candidate set.",
            "screenshot_paths":
                "Local annotated screenshot paths to be "
                "attached as images during the API request."
        },
        "original_predictions": predictions,
        "ground_truth": ground_truth,
        "ground_truth_severity": severity,
        "navigation_order": navigation,
        "kfg": kfg,
        "evaluated_transitions": inventory[subject],
        "screenshot_paths": [
            str(p.resolve()) for p in screenshots
        ]
    }


def main():
    inventory = load_inventory()
    subjects = sorted(inventory)

    if len(subjects) != 37:
        raise ValueError(
            f"Expected 37 subjects, found {len(subjects)}"
        )

    packages = {}
    failures = []

    for subject in subjects:
        try:
            packages[subject] = prepare_subject(
                subject, inventory
            )
            print(f"[OK] {subject}")
        except (OSError, ValueError, KeyError) as e:
            failures.append((subject, str(e)))
            print(f"[MISSING/INVALID] {subject}: {e}")

    if failures:
        print("\nCannot create a complete dataset.")
        print("Resolve the missing inputs first.")
        return

    if OUTPUT_ROOT.exists():
        raise FileExistsError(
            f"Output directory already exists: {OUTPUT_ROOT}. "
            "Preserve existing results before regenerating."
        )

    OUTPUT_ROOT.mkdir(parents=True)

    for subject, data in packages.items():
        path = OUTPUT_ROOT / f"{subject}.json"

        with path.open("w", encoding="utf-8") as f:
            json.dump(data, f, indent=2)

    print("\nDataset preparation complete.")
    print("Subjects:", len(packages))
    print("Total evaluated transitions:",
          sum(len(d["evaluated_transitions"])
              for d in packages.values()))
    print("Total screenshots:",
          sum(len(d["screenshot_paths"])
              for d in packages.values()))
    print("Output directory:", OUTPUT_ROOT)

    print("\nInput sizes:")
    for path in sorted(OUTPUT_ROOT.glob("*.json")):
        size_kb = path.stat().st_size / 1024
        print(f"  {path.stem}: {size_kb:.1f} KB")


if __name__ == "__main__":
    main()