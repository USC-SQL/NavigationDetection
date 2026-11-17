import csv
import json
from collections import Counter
from pathlib import Path

BASE = Path("Evaluation_Scripts/LNF_Error_Pattern_Discovery")
SOURCE = BASE / "subject_inputs"
OUTPUT = BASE / "matching_inputs"
DEFINITIONS = BASE / "matching_pattern_definitions.json"
PRIVATE_KEY = BASE / "matching_evaluation_key.csv"
PATTERN_MAP = BASE / "matching_pattern_id_map.json"

# Neutral descriptions based on the discovered taxonomy.
# The matcher must not know which patterns were FP or FN.
PATTERNS = [
    (
        "P01", "FP01",
        "Adjacent cross-region continuation",
        "A direct consecutive TAB transition between distinct "
        "visible page regions, with adjacent inferred meaningful "
        "positions and no documented intermediate focus target."
    ),
    (
        "P02", "FP02",
        "Cyclic or wraparound focus edge",
        "A TAB transition from a later or end-of-page control "
        "back to an earlier control, often near the top or "
        "beginning of the page."
    ),
    (
        "P03", "FP03",
        "Local auxiliary-control progression",
        "A transition between nearby support, utility, or "
        "auxiliary controls within the same functional group, "
        "with a consecutive or locally coherent focus sequence."
    ),
    (
        "P04", "FN01",
        "Browser-to-page entry",
        "A modeled transition from the virtual Browser boundary "
        "to the documented first focused webpage control."
    ),
    (
        "P05", "FN02",
        "Consecutive cross-subgroup transition",
        "A direct consecutive TAB transition whose endpoints "
        "belong to distinct visible or functional subgroups. "
        "Inferred meaningful positions are adjacent or nearby."
    ),
    (
        "P06", "FN03",
        "Field-to-field transition near embedded helper link",
        "A direct TAB transition between primary form fields "
        "where a separate focusable helper or recovery link "
        "is visually or structurally associated with the "
        "destination field group."
    ),
    (
        "P07", "FN04",
        "Auxiliary-link transition across workflow controls",
        "A direct TAB transition originating from a helper, "
        "recovery, or support link, with one or more primary "
        "workflow controls located between its endpoints "
        "in the inferred meaningful or visual order."
    ),
    (
        "P08", "FN05",
        "Primary-control transition over local controls",
        "A direct TAB transition from a primary control to "
        "a later control in the same functional module, "
        "with one or more visible intermediate controls "
        "between them in meaningful or visual order."
    ),
    (
        "P09", "FN06",
        "Backward local focus transition",
        "A TAB transition within the same local component "
        "or content cluster where the destination has an "
        "earlier inferred meaningful position than the source."
    ),
]


def write_json(path, data):
    with path.open("x", encoding="utf-8") as f:
        json.dump(data, f, indent=2)


def main():
    if OUTPUT.exists() or DEFINITIONS.exists():
        raise FileExistsError(
            "Matching inputs or definitions already exist. "
            "Preserve earlier results before regenerating."
        )

    if PRIVATE_KEY.exists() or PATTERN_MAP.exists():
        raise FileExistsError(
            "Private evaluation keys already exist."
        )

    input_files = sorted(SOURCE.glob("*.json"))

    if len(input_files) != 37:
        raise ValueError(
            f"Expected 37 subjects, found {len(input_files)}"
        )

    OUTPUT.mkdir(parents=True)

    definitions = []
    mapping = []

    for neutral_id, original_id, name, criteria in PATTERNS:
        definitions.append({
            "pattern_id": neutral_id,
            "name": name,
            "observable_criteria": criteria,
        })

        mapping.append({
            "neutral_id": neutral_id,
            "discovered_id": original_id,
        })

    write_json(DEFINITIONS, {
        "purpose": "Outcome-blinded pattern matching",
        "patterns": definitions,
    })

    write_json(PATTERN_MAP, mapping)

    evaluation_key = []
    totals = Counter()
    screenshot_count = 0

    for input_path in input_files:
        with input_path.open(encoding="utf-8") as f:
            original = json.load(f)

        subject = original["subject"]

        if subject != input_path.stem:
            raise ValueError(
                f"Subject mismatch: {input_path}"
            )

        candidates = []

        for index, row in enumerate(
            original["evaluated_transitions"], 1
        ):
            transition_id = f"T{index:04d}"

            # No classification, prediction status,
            # ground-truth severity, or error explanation.
            candidates.append({
                "transition_id": transition_id,
                "transition": row["transition"],
                "source_label": row["source_label"],
                "destination_label":
                    row["destination_label"],
                "source_position":
                    row["source_position"],
                "destination_position":
                    row["destination_position"],
                "source_descriptor":
                    row["source_descriptor"],
                "destination_descriptor":
                    row["destination_descriptor"],
                "observed_in_stage1":
                    row["observed_in_stage1"],
                "browser_boundary":
                    row["browser_boundary"],
            })

            evaluation_key.append({
                "subject": subject,
                "transition_id": transition_id,
                "transition": row["transition"],
                "classification": row["classification"],
            })

            totals[row["classification"]] += 1

        screenshots = original["screenshot_paths"]
        screenshot_count += len(screenshots)

        matching_input = {
            "subject": subject,
            "navigation_order": original["navigation_order"],
            "kfg": original["kfg"],
            "screenshot_paths": screenshots,
            "candidate_transitions": candidates,
        }

        write_json(
            OUTPUT / f"{subject}.json",
            matching_input
        )

        print(
            f"[OK] {subject}: "
            f"{len(candidates)} candidate transitions"
        )

    expected = {
        "TP": 41,
        "FP": 14,
        "FN": 52,
        "TN": 753,
    }

    for label, count in expected.items():
        if totals[label] != count:
            raise ValueError(
                f"{label}: expected {count}, "
                f"found {totals[label]}"
            )

    with PRIVATE_KEY.open(
        "x", newline="", encoding="utf-8-sig"
    ) as f:
        writer = csv.DictWriter(
            f,
            fieldnames=[
                "subject",
                "transition_id",
                "transition",
                "classification",
            ],
        )
        writer.writeheader()
        writer.writerows(evaluation_key)

    print("\n" + "=" * 50)
    print("BLINDED MATCHING INPUT SUMMARY")
    print("=" * 50)
    print("Subjects:", len(input_files))
    print("Transitions:", sum(totals.values()))
    print("Screenshots:", screenshot_count)
    print("Neutral pattern definitions:", len(definitions))
    print("Evaluation counts (private key only):")
    for label in ("TP", "FP", "FN", "TN"):
        print(f"  {label}: {totals[label]}")
    print("Matching inputs:", OUTPUT)
    print("Definitions:", DEFINITIONS)
    print("Private evaluation key:", PRIVATE_KEY)


if __name__ == "__main__":
    main()
