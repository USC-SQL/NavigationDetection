import argparse
import json
from pathlib import Path

import utils


CATEGORIES = ("Strongly", "Slightly", "Neutral")


def read_edges(path):
    if not path.is_file():
        raise FileNotFoundError(f"Missing file: {path}")

    return {
        "".join(line.split())
        for line in path.read_text(encoding="utf-8-sig").splitlines()
        if line.strip()
    }


def prepare_subject(subject, prediction_root, gt_root,
                    navigation_root, output_root):

    prediction_file = prediction_root / subject / f"{subject}.txt"
    navigation_file = navigation_root / subject / f"{subject}.json"
    gt_folder = gt_root / "processed_groundtruth" / subject

    predicted = read_edges(prediction_file)
    ground_truth = read_edges(gt_folder / "xpath_failures.txt")

    categories = {
        category: read_edges(gt_folder / f"{category}.txt")
        for category in CATEGORIES
    }

    if not navigation_file.is_file():
        raise FileNotFoundError(f"Missing file: {navigation_file}")

    navigation_json = json.loads(
        navigation_file.read_text(encoding="utf-8")
    )

    elements = {
        element["xpath"]: element
        for element in navigation_json["navigation_order"]
    }

    transitions = []

    for edge in sorted(predicted | ground_truth):
        if edge in predicted and edge in ground_truth:
            classification = "TP"
        elif edge in predicted:
            classification = "FP"
        else:
            classification = "FN"

        parts = edge.split("->")

        if len(parts) != 2:
            raise ValueError(
                f"Invalid transition for {subject}: {edge}"
            )

        source, destination = parts

        category = next(
            (
                name for name in CATEGORIES
                if edge in categories[name]
            ),
            None
        )

        transitions.append({
            "transition": f"{source} -> {destination}",
            "classification": classification,
            "ground_truth_category": category,
            "source_element": elements.get(source, {
                "xpath": source,
                "descriptor": "Browser or unavailable in Stage 1"
            }),
            "destination_element": elements.get(destination, {
                "xpath": destination,
                "descriptor": "Browser or unavailable in Stage 1"
            })
        })

    result = {
        "subject": subject,
        "navigation_order": navigation_json["navigation_order"],
        "transition_analysis_input": transitions
    }

    subject_dir = output_root / subject
    subject_dir.mkdir(parents=True, exist_ok=True)

    output_file = subject_dir / f"{subject}.json"

    output_file.write_text(
        json.dumps(result, indent=2),
        encoding="utf-8"
    )

    return {
        "TP": sum(t["classification"] == "TP" for t in transitions),
        "FP": sum(t["classification"] == "FP" for t in transitions),
        "FN": sum(t["classification"] == "FN" for t in transitions)
    }


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "--navigation-root",
        type=Path,
        required=True
    )

    args = parser.parse_args()

    configs = utils.get_config()
    subjects = utils.get_subjects(configs["path_to_subjects"])

    prediction_root = Path(
        configs["path_to_processed_related_results"]
    )

    gt_root = Path(configs["path_to_gt"])

    output_root = (
        Path(__file__).resolve().parent / "LLM_Analysis_Input"
    )

    totals = {"TP": 0, "FP": 0, "FN": 0}

    for subject in subjects:
        counts = prepare_subject(
            subject,
            prediction_root,
            gt_root,
            args.navigation_root,
            output_root
        )

        for key in totals:
            totals[key] += counts[key]

        print(
            f"{subject}: "
            f"TP={counts['TP']}, "
            f"FP={counts['FP']}, "
            f"FN={counts['FN']}"
        )

    print("\nAnalysis input preparation complete.")
    print(f"Subjects: {len(subjects)}")
    print(f"TP: {totals['TP']}")
    print(f"FP: {totals['FP']}")
    print(f"FN: {totals['FN']}")
    print(f"Output directory: {output_root}")


if __name__ == "__main__":
    main()
