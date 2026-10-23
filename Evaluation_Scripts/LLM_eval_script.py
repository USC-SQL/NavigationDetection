import csv
from pathlib import Path

import pandas as pd
import utils


CATEGORIES = ("Strongly", "Slightly", "Neutral")


def read_edges(path):
    """Read and normalize XPath transitions."""
    if not path.is_file():
        raise FileNotFoundError(f"Missing evaluation file: {path}")

    with path.open("r", encoding="utf-8-sig") as file:
        return {
            line.replace(" ", "").strip()
            for line in file
            if line.strip()
        }


def evaluate(subjects, prediction_root, gt_root, exclude_browser=False):
    rows = []

    total_tp = 0
    total_fp = 0
    total_fn = 0

    for subject in subjects:
        prediction_file = (
            prediction_root / subject / f"{subject}.txt"
        )

        gt_folder = gt_root / "processed_groundtruth" / subject

        predicted = read_edges(prediction_file)
        ground_truth = read_edges(gt_folder / "xpath_failures.txt")

        category_edges = {
            category: read_edges(gt_folder / f"{category}.txt")
            for category in CATEGORIES
        }

        if exclude_browser:
            predicted = {
                edge for edge in predicted
                if "browser" not in edge.lower()
            }

            ground_truth = {
                edge for edge in ground_truth
                if "browser" not in edge.lower()
            }

            category_edges = {
                category: {
                    edge for edge in edges
                    if "browser" not in edge.lower()
                }
                for category, edges in category_edges.items()
            }

        tp_edges = predicted & ground_truth
        fp_edges = predicted - ground_truth
        fn_edges = ground_truth - predicted

        tp = len(tp_edges)
        fp = len(fp_edges)
        fn = len(fn_edges)

        total_tp += tp
        total_fp += fp
        total_fn += fn

        row = {
            "Subject": subject,
            "TP": tp,
            "FP": fp,
            "FN": fn,
        }

        for category in CATEGORIES:
            edges = category_edges[category]

            row[f"{category} TP"] = len(predicted & edges)
            row[f"{category} FN"] = len(edges - predicted)

        row["Precision"] = tp / (tp + fp) if tp + fp else None
        row["Recall"] = tp / (tp + fn) if tp + fn else None

        rows.append(row)

    precision = (
        total_tp / (total_tp + total_fp)
        if total_tp + total_fp else None
    )

    recall = (
        total_tp / (total_tp + total_fn)
        if total_tp + total_fn else None
    )

    f1 = (
        2 * precision * recall / (precision + recall)
        if precision is not None
        and recall is not None
        and precision + recall > 0
        else None
    )

    rows.append({
        "Subject": "TOTAL",
        "TP": total_tp,
        "FP": total_fp,
        "FN": total_fn,
        "Precision": precision,
        "Recall": recall,
    })

    return pd.DataFrame(rows), precision, recall, f1


def main():
    configs = utils.get_config()
    subjects = utils.get_subjects(configs["path_to_subjects"])

    prediction_root = Path(
        configs["path_to_processed_related_results"]
    )

    gt_root = Path(configs["path_to_gt"])

    output_dir = Path(__file__).resolve().parent

    for exclude_browser, filename in [
        (False, "LLM_evaluation_full.csv"),
        (True, "LLM_evaluation_xpath_only.csv"),
    ]:
        dataframe, precision, recall, f1 = evaluate(
            subjects,
            prediction_root,
            gt_root,
            exclude_browser=exclude_browser,
        )

        output_path = output_dir / filename
        dataframe.to_csv(output_path, index=False)

        print(f"\nSaved: {output_path}")
        print(f"Precision: {precision}")
        print(f"Recall: {recall}")
        print(f"F1: {f1}")


if __name__ == "__main__":
    main()
