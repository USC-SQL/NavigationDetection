import csv
import json
import os
import utils
import pandas as pd
from pathlib import Path

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
    df.to_excel("InsertToolNameHereResults.xlsx", index=False, engine="xlsxwriter")

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

def get_nodes(subject, path, tool):
    path_to_subject = path + "/" + tool + "/" + subject + "/output.csv"
    nodes = set()
    with open(path_to_subject, mode='r', newline='') as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:
            raw_node = row[0]
            node = raw_node.replace(" ", "")
            nodes.add(node)
    return nodes

def comparison(subjects, path_to_processed_results, path_to_gt, tool):
    path_to_gt = path_to_gt + "\\processed_groundtruth"
    types = ["Strongly", "Slightly", "Neutral"]
    #types = ["Neutral", "Slightly", "Strongly"]
    data = {}
    for subject in subjects:
        output_nodes = get_nodes(subject, path_to_processed_results, tool)
        if(len(output_nodes) > 0):
            print(subject)
        subject_data = {}
        FP = 0
        correct_TP_nodes = set()
        correct_TP_edges = set()
        for type in types:
            gt_edges = get_edges(subject, path_to_gt, type)
            TP_count = 0
            for edge in gt_edges:
                source, target = edge.split("->")
                if source in output_nodes or target in output_nodes:
                    TP_count = TP_count + 1
                    correct_TP_edges.add(edge)
                    if(source in output_nodes):
                        correct_TP_nodes.add(source)
                    if(target in output_nodes):
                        correct_TP_nodes.add(target)
                    break
                #for node in output_nodes:
                #    if node == source or node == target:
                #        TP_count = TP_count + 1
                        #if(target not in output_nodes):
                        #    FP += 1
                #        break
                    #if node == target:
                    #    TP_count = TP_count + 1
                        #if(source not in output_nodes):
                        #    FP += 1
                        #break
            for edge in correct_TP_edges:
                source, target = edge.split("->")
                if(source in correct_TP_nodes and target in correct_TP_nodes):
                    continue
                else:
                    FP = FP + 1
            subject_data[type + " " + "TP"] = TP_count #TP per type
            subject_data[type + " " + "FN"] = len(gt_edges) - TP_count #FN per type
        all_gt_edges = get_edges(subject, path_to_gt, "xpath_failures")
        total_detected = subject_data["Strongly TP"] + subject_data["Slightly TP"] + subject_data["Neutral TP"]
        incorrect_nodes = output_nodes.difference(correct_TP_nodes)
        subject_data["FP"] = 2 * len(incorrect_nodes) + FP # - total_detected + FP
        data[subject] = subject_data
    write_subject_res_to_xlsx(data)

if __name__ == '__main__':
    configs = utils.get_config()
    subjects = subjects = utils.get_subjects(configs['path_to_subjects'])
    tool = 'BAGEL' #Select which related work tool you want to evaluate here
    comparison(subjects, configs['path_to_processed_related_results'], configs['path_to_gt'], tool)