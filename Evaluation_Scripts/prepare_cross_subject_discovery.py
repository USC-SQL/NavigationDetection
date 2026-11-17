import json
from collections import Counter
from pathlib import Path

BASE = Path(
    "Evaluation_Scripts/LNF_Error_Pattern_Discovery"
)

INPUT_DIR = BASE / "subject_inputs"
ANALYSIS_DIR = BASE / "subject_analysis"
OUTPUT = BASE / "cross_subject_input.json"

EXPECTED_SUBJECTS = 37
EXPECTED_FP = 14
EXPECTED_FN = 52


def normalize(transition):
    value = "".join(transition.split())
    parts = value.split("->")

    if len(parts) != 2 or not all(parts):
        raise ValueError(
            f"Invalid transition: {transition}"
        )

    return " -> ".join(parts)


def load_json(path):
    with path.open(encoding="utf-8") as f:
        return json.load(f)


def main():
    input_files = sorted(INPUT_DIR.glob("*.json"))
    analysis_files = sorted(ANALYSIS_DIR.glob("*.json"))

    if len(input_files) != EXPECTED_SUBJECTS:
        raise ValueError(
            f"Expected 37 subject inputs, found {len(input_files)}"
        )

    if len(analysis_files) != EXPECTED_SUBJECTS:
        raise ValueError(
            f"Expected 37 subject analyses, found {len(analysis_files)}"
        )

    if {p.stem for p in input_files} != {
        p.stem for p in analysis_files
    }:
        raise ValueError(
            "Subject input and output filenames do not match."
        )

    cases = []
    subject_summaries = []

    for path in analysis_files:
        subject = path.stem

        analysis = load_json(path)
        original = load_json(
            INPUT_DIR / f"{subject}.json"
        )

        if analysis["subject"] != subject:
            raise ValueError(
                f"Subject mismatch: {subject}"
            )

        inventory = {
            normalize(row["transition"]): row
            for row in original["evaluated_transitions"]
        }

        expected = {
            (transition, row["classification"])
            for transition, row in inventory.items()
            if row["classification"] in ("FP", "FN")
        }

        actual = [
            (
                normalize(error["transition"]),
                error["classification"]
            )
            for error in analysis["error_analyses"]
        ]

        if len(actual) != len(set(actual)):
            raise ValueError(
                f"Duplicate error analyses: {subject}"
            )

        if set(actual) != expected:
            raise ValueError(
                f"FP/FN mismatch for subject: {subject}"
            )

        subject_summaries.append({
            "subject": subject,
            "summary": analysis["subject_summary"]
        })

        for error in analysis["error_analyses"]:
            transition = normalize(
                error["transition"]
            )

            record = inventory[transition]

            case_id = f"E{len(cases) + 1:03d}"

            cases.append({
                "case_id": case_id,
                "subject": subject,
                "classification": error["classification"],
                "transition": transition,
                "source_descriptor":
                    record["source_descriptor"],
                "destination_descriptor":
                    record["destination_descriptor"],
                "source_position":
                    record["source_position"],
                "destination_position":
                    record["destination_position"],
                "pattern_hypothesis":
                    error["pattern_hypothesis"],
                "observable_characteristics":
                    error["observable_characteristics"],
                "matching_criteria":
                    error["matching_criteria"],
                "possible_cause":
                    error["possible_cause"],
                "supporting_evidence":
                    error["supporting_evidence"],
                "confidence":
                    error["confidence"],
                "requires_review":
                    error["requires_review"]
            })

        print(
            f"[OK] {subject}: "
            f"{len(actual)} error analyses"
        )

    counts = Counter(
        case["classification"] for case in cases
    )

    if counts["FP"] != EXPECTED_FP:
        raise ValueError(
            f"Expected 14 FPs, found {counts['FP']}"
        )

    if counts["FN"] != EXPECTED_FN:
        raise ValueError(
            f"Expected 52 FNs, found {counts['FN']}"
        )

    result = {
        "analysis_type": "independent_cross_subject_discovery",
        "instructions": (
            "Discover recurring error characteristics "
            "independently. No manual pattern taxonomy "
            "has been supplied. Error IDs uniquely identify "
            "cases from the original evaluation."
        ),
        "total_subjects": len(analysis_files),
        "total_fp": counts["FP"],
        "total_fn": counts["FN"],
        "subject_summaries": subject_summaries,
        "error_cases": cases
    }

    if OUTPUT.exists():
        raise FileExistsError(
            f"Output already exists: {OUTPUT}"
        )

    with OUTPUT.open("x", encoding="utf-8") as f:
        json.dump(result, f, indent=2)

    print("\n" + "=" * 50)
    print("CROSS-SUBJECT INPUT SUMMARY")
    print("=" * 50)
    print("Subjects:", len(analysis_files))
    print("False positives:", counts["FP"])
    print("False negatives:", counts["FN"])
    print("Total error cases:", len(cases))
    print("Output:", OUTPUT)
    print(
        "Input size:",
        f"{OUTPUT.stat().st_size / 1024:.1f} KB"
    )


if __name__ == "__main__":
    main()