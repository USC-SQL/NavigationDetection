import csv
import json
import os
import utils
import pandas as pd
from pathlib import Path

def write_FNs(total_FN, subject):
    filename = subject + ".txt"
    filepath = os.path.join("FNs", filename)
    with open(filepath, "w", encoding="utf-8") as f:
        for fn in total_FN:
            f.write(fn + "\n")

def write_subject_res_to_xlsx(data):
    field_names = ['Subject', 'Strongly TP', 'Strongly FN', 'Slightly TP', 'Slightly FN', 'Neutral TP', 'Neutral FN', 'FP']
    write_data = []
    for subject in subjects:
        subject_dict = data[subject]
        subject_data = []
        subject_data.append(subject)
        subject_data.append(subject_dict['Strongly TP'])
        subject_data.append(subject_dict['Strongly FN'])
        subject_data.append(subject_dict['Slightly TP'])
        subject_data.append(subject_dict['Slightly FN'])
        subject_data.append(subject_dict['Neutral TP'])
        subject_data.append(subject_dict['Neutral FN'])
        subject_data.append(subject_dict['FP'])
        write_data.append(subject_data)
    df = pd.DataFrame(write_data, columns=field_names)
    df.to_excel("Test.xlsx", index=False, engine="xlsxwriter")

def get_edges(subject, path, type):
    path_to_subject = path + "/" + subject + "/" + type + ".txt"
    edges = set()
    with open(path_to_subject, mode='r', newline='') as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:
            raw_edge = row[0]
            edge = raw_edge.replace(" ", "")
            edges.add(edge)
    return edges

def comparison(subjects, path_to_processed_results, path_to_gt):
    path_to_gt = path_to_gt + "\\processed_groundtruth"
    types = ["Strongly", "Slightly", "Neutral"]
    data = {}
    for subject in subjects:
        print()
        #print(subject)
        total_FN = set()
        detected_edges = get_edges(subject, path_to_processed_results, "DetectedLNFs")
        subject_data = {}
        for type in types:
            type_FN = set()
            gt_edges = get_edges(subject, path_to_gt, type)
            #print(str(type) + ": " + str(len(gt_edges)))
            subject_data[type + " " + "TP"] = len(detected_edges.intersection(gt_edges)) #TP per type
            subject_data[type + " " + "FN"] = len(gt_edges.difference(detected_edges)) #FN per type
            type_FN = gt_edges.difference(detected_edges)
            total_FN.update(type_FN)
        #write_FNs(total_FN, subject)
        all_gt_edges = get_edges(subject, path_to_gt, "xpath_failures")
        print(subject + ": " + str(len(all_gt_edges)))
        subject_data["FP"] = len(detected_edges.difference(all_gt_edges)) #FP per subject
        print("FP: " + str(detected_edges.difference(all_gt_edges)))
        data[subject] = subject_data
    #write_subject_res_to_xlsx(data)

#Calculate LYNX evaluation
if __name__ == '__main__':
    configs = utils.get_config()
    subjects = subjects = utils.get_subjects(configs['path_to_subjects'])
    comparison(subjects, configs['path_to_processed_LYNX_results'], configs['path_to_gt'])