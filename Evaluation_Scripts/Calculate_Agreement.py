import csv
import json
import os
import utils
import pandas as pd
from pathlib import Path
import numpy as np
import krippendorff

ratingdict = {
  "Strongly Disagree": 1,
  "Slightly Disagree": 2,
  "Neutral": 3,
  "Slightly Agree": 4,
  "Strongly Agree": 5
}

def GetLNFs(subject_name, path_to_gt):
    filename = path_to_gt + "\\raw_groundtruth\\" + subject_name + "\\num_failures.txt"
    LNFs = set()
    with open(filename, "r", encoding="utf-8") as f:
        for line in f:
            line = line.strip().replace(" ", "")
            if line:  # skip empty lines, remove if you want to keep them
                LNFs.add(line)
    print(LNFs)
    return LNFs

def NormalizeShortResponse(column_header, all_items):
    new_all_items = all_items
    Browser = False
    if "(" in column_header:
        cutoffend = ")".join(column_header.split(")", 2)[:2])
        startindex = cutoffend.index('(')
        range =  cutoffend[startindex+1:]
        split_range = range.split()
        start = split_range[0]
        end = split_range[-1]
        if(end == "Browser"):
            Browser = True
            end = split_range[4].split("(")[1:][0]
        count = int(start)
        while(count < int(end)):
            sequence = str(count) + " - > " + str(count + 1)
            new_all_items.append([3, 0, 0, 0, 0])
            count += 1
        if(Browser):
            last_sequence = end + " - > Browser"
            new_all_items.append([3, 0, 0, 0, 0])
    return new_all_items

def calculate(root_folder, subjects, path_to_gt):
    # Create a list to store results
    all_alphas = []
    #all_items = []
    # Walk through each sub-folder in the root folder
    for subdir, _, files in os.walk(root_folder):
        # Check if there is any .xlsx file in the sub-folder
        for file in files:
            if file.endswith('.xlsx'):
                all_items = []
                file_path = os.path.join(subdir, file)
                try:
                    df = pd.read_excel(file_path, engine = 'openpyxl')
                    subject_name = os.path.basename(subdir)
                    if(subject_name not in subjects):
                        continue
                    for column_header in df.columns:
                        if column_header == "Timestamp":
                            continue
                        if "(" in column_header:
                            all_items = NormalizeShortResponse(column_header, all_items)
                            continue
                        else:
                            ratings = df[column_header].map(ratingdict).dropna().astype(int)
                            #counts = [sum(ratings == k) for k in range(1, 6)]
                            binned_ratings = ratings.apply(lambda x: 0 if x == 1 else 1)
                            counts = [sum(binned_ratings == 0), sum(binned_ratings == 1)]
                        all_items.append(counts)
                except Exception as e:
                    print("Error: " + os.path.basename(subdir))
                    print(e)
                #'''
                expanded_items = []
                for counts in all_items:
                    expanded = []
                    for category, c in enumerate(counts, start=1):
                        expanded.extend([category] * c)
                    expanded_items.append(expanded)
                #Checks to see if all raters have perfect agreement for a given subject
                flat_values = [r for v in expanded_items for r in v]
                unique_values = set(flat_values)
                if len(unique_values) < 2:
                    # All raters gave exactly the same rating
                    print(f"{subject_name}: only one unique value → alpha=1.0")
                    all_alphas.append(1.0)
                    continue
                max_len = max(len(v) for v in expanded_items)
                data = pd.DataFrame([v + [np.nan]*(max_len-len(v)) for v in expanded_items])
                alpha = krippendorff.alpha(
                    reliability_data=data.T.values,
                    level_of_measurement="ordinal"
                )
                all_alphas.append(alpha)
    total_sum = sum(all_alphas)
    average = total_sum / len(all_alphas)
    return average
    #'''
    '''
    expanded_items = []
    for counts in all_items:
        expanded = []
        for category, c in enumerate(counts, start=1):
            expanded.extend([category] * c)
        expanded_items.append(expanded)
    max_len = max(len(v) for v in expanded_items)
    data = pd.DataFrame([v + [np.nan]*(max_len-len(v)) for v in expanded_items])
    alpha = krippendorff.alpha(
        reliability_data=data.T.values,
        level_of_measurement="interval"
    )
    return alpha
    '''

    
#Used to calculate rater agreement
if __name__ == '__main__':
    configs = utils.get_config()
    subjects = utils.get_subjects(configs['path_to_subjects'])
    overall_kappa = calculate(configs['path_to_surveys'], subjects, configs['path_to_gt'])
    print("Test: " + str(overall_kappa))