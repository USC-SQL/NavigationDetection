import csv
import json
from collections import defaultdict
from pathlib import Path

BASE = Path(
    "Evaluation_Scripts/LNF_Error_Pattern_Discovery"
)

INPUT = BASE / "cross_subject_input.json"
PATTERNS = BASE / "cross_subject_patterns.json"
OUTPUT = BASE / "results"


def load_json(path):
    with path.open(encoding="utf-8") as f:
        return json.load(f)


def write_csv(path, rows, fields):
    with path.open(
        "w", newline="", encoding="utf-8-sig"
    ) as f:
        writer = csv.DictWriter(f, fieldnames=fields)
        writer.writeheader()
        writer.writerows(rows)


def main():
    data = load_json(INPUT)
    discovery = load_json(PATTERNS)

    cases = {
        case["case_id"]: case
        for case in data["error_cases"]
    }

    if len(cases) != 66:
        raise ValueError("Expected 66 unique error cases")

    patterns = discovery["patterns"]
    unclassified = set(
        discovery["unclassified_case_ids"]
    )

    covered = set()
    membership_count = 0

    for pattern in patterns:
        ids = pattern["matched_case_ids"]

        if len(ids) != len(set(ids)):
            raise ValueError(
                f"Duplicate IDs in {pattern['pattern_id']}"
            )

        for case_id in ids:
            if case_id not in cases:
                raise ValueError(
                    f"Unknown case ID: {case_id}"
                )

            if (
                cases[case_id]["classification"]
                != pattern["error_type"]
            ):
                raise ValueError(
                    f"Classification mismatch: {case_id}"
                )

            covered.add(case_id)
            membership_count += 1

    if covered & unclassified:
        raise ValueError(
            "An unclassified case was assigned to a pattern"
        )

    if covered | unclassified != set(cases):
        raise ValueError(
            "Not all 66 errors were accounted for"
        )

    if OUTPUT.exists():
        raise FileExistsError(
            f"Results directory already exists: {OUTPUT}. "
            "Preserve previous outputs."
        )

    OUTPUT.mkdir(parents=True)

    summary_rows = []
    membership_rows = []

    for pattern in patterns:
        pattern_id = pattern["pattern_id"]
        error_type = pattern["error_type"]

        matched = [
            cases[case_id]
            for case_id in pattern["matched_case_ids"]
        ]

        subjects = {
            case["subject"] for case in matched
        }

        denominator = (
            data["total_fp"]
            if error_type == "FP"
            else data["total_fn"]
        )

        error_share = (
            100 * len(matched) / denominator
        )

        summary_rows.append({
            "pattern_id": pattern_id,
            "error_type": error_type,
            "name": pattern["name"],
            "short_summary": pattern["short_summary"],
            "matching_criteria": pattern["matching_criteria"],
            "hypothesized_cause":
                pattern["hypothesized_cause"],
            "uncertainty": pattern["uncertainty"],
            "matched_error_cases": len(matched),
            "distinct_subjects": len(subjects),
            "all_errors_of_type": denominator,
            "share_of_error_type_percent":
                round(error_share, 2),
            # These require full-dataset matching later.
            "total_pattern_occurrences": "",
            "pattern_error_rate_percent": "",
        })

        by_subject = defaultdict(list)

        for case in matched:
            by_subject[case["subject"]].append(case)

            membership_rows.append({
                "pattern_id": pattern_id,
                "pattern_name": pattern["name"],
                "case_id": case["case_id"],
                "subject": case["subject"],
                "classification": case["classification"],
                "transition": case["transition"],
                "confidence": case["confidence"],
                "requires_review": case["requires_review"],
            })

        for subject, subject_cases in by_subject.items():
            folder = (
                OUTPUT / "patterns" / pattern_id / subject
            )
            folder.mkdir(parents=True, exist_ok=True)

            path = folder / "matching_sequences.txt"

            lines = [
                f"Pattern: {pattern_id}",
                f"Name: {pattern['name']}",
                f"Error type: {error_type}",
                f"Subject: {subject}",
                "",
                f"Summary: {pattern['short_summary']}",
                "",
                f"Matching criteria: "
                f"{pattern['matching_criteria']}",
                "",
                "MATCHED ERROR TRANSITIONS",
                "=" * 60,
            ]

            for case in subject_cases:
                lines.extend([
                    "",
                    f"Case ID: {case['case_id']}",
                    f"Classification: "
                    f"{case['classification']}",
                    f"Transition: {case['transition']}",
                    f"Source: {case['source_descriptor']}",
                    f"Destination: "
                    f"{case['destination_descriptor']}",
                    f"Positions: "
                    f"{case['source_position']} -> "
                    f"{case['destination_position']}",
                    f"Supporting evidence: "
                    f"{case['supporting_evidence']}",
                    f"Confidence: {case['confidence']}",
                    f"Requires review: "
                    f"{case['requires_review']}",
                ])

            path.write_text(
                "\n".join(lines) + "\n",
                encoding="utf-8"
            )

    summary_fields = [
        "pattern_id",
        "error_type",
        "name",
        "short_summary",
        "matching_criteria",
        "hypothesized_cause",
        "uncertainty",
        "matched_error_cases",
        "distinct_subjects",
        "all_errors_of_type",
        "share_of_error_type_percent",
        "total_pattern_occurrences",
        "pattern_error_rate_percent",
    ]

    write_csv(
        OUTPUT / "pattern_summary.csv",
        summary_rows,
        summary_fields
    )

    write_csv(
        OUTPUT / "pattern_case_memberships.csv",
        membership_rows,
        [
            "pattern_id",
            "pattern_name",
            "case_id",
            "subject",
            "classification",
            "transition",
            "confidence",
            "requires_review",
        ]
    )

    unclassified_rows = [
        cases[case_id]
        for case_id in sorted(unclassified)
    ]

    write_csv(
        OUTPUT / "unclassified_errors.csv",
        unclassified_rows,
        list(cases[next(iter(cases))].keys())
    )

    print("=" * 55)
    print("PATTERN RESULTS SUMMARY")
    print("=" * 55)

    for row in summary_rows:
        print(
            f"{row['pattern_id']}: "
            f"{row['matched_error_cases']} "
            f"{row['error_type']} cases, "
            f"{row['distinct_subjects']} subjects"
        )

    print("\nDistinct errors assigned:", len(covered))
    print("Total pattern memberships:", membership_count)
    print("Unclassified errors:", len(unclassified))
    print("Discovered patterns:", len(patterns))

    files = list(
        (OUTPUT / "patterns").rglob(
            "matching_sequences.txt"
        )
    )

    print("Raw matching-sequence files:", len(files))
    print("Saved results to:", OUTPUT)


if __name__ == "__main__":
    main()
