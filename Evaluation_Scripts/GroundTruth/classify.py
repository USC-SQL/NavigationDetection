import os
import pandas as pd
import openpyxl
import csv
from collections import Counter


ratingdict = {
  "Strongly Disagree": 1,
  "Slightly Disagree": 2,
  "Neutral": 3,
  "Slightly Agree": 4,
  "Strongly Agree": 5
}

def write_gt_to_txt(gt, outputfile):
    # Open the file in write mode ('w')
    with open(outputfile, "w") as f:
        # Iterate over the lines and write each one to the file
        for line in gt:
            f.write(line + "\n")  # Add a newline character after each line


def writw_to_csv(my_dict, outputfile):
    with open(outputfile, "a", newline="") as f:
        w = csv.DictWriter(f, my_dict.keys())
        w.writeheader()
        w.writerow(my_dict)

def NormalizeShortResponse(column_header, results):
    new_results = results
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
            new_results[sequence] = 1
            count += 1
        if(Browser):
            last_sequence = end + " - > Browser"
            new_results[last_sequence] = 1
    return new_results

def process_xlsx_files_in_subfolders(root_folder, output_csv):
    # Create a list to store results
    results = []
    # Walk through each sub-folder in the root folder
    for subdir, _, files in os.walk(root_folder):
        # Check if there is any .xlsx file in the sub-folder
        for file in files:
            if file.endswith('.xlsx'):
                file_path = os.path.join(subdir, file)
                try:
                    #Read the .xlsx file
                    df = pd.read_excel(file_path, engine = 'openpyxl')
                    results = {}

                    bucket1 = []
                    bucket2 = []
                    bucket3 = []

                    # Extract the sub-folder name
                    subject_name = os.path.basename(subdir)
                    results[''] = subject_name

                    for column_header in df.columns:
                        if column_header == "Timestamp":
                            continue
                        if "(" in column_header:
                            results = NormalizeShortResponse(column_header, results)
                            continue
                        start = column_header.find("[") + 1
                        end = column_header.find("]")
                        substring = column_header[start:end]

                        total_rating = 0
                        #total_rating = [] #used for weighted average
                        num_rows = 0
                        for index, row in df.iterrows():
                            if pd.isnull(row[column_header]):
                                continue
                            #total_rating.append(ratingdict[row[column_header]]) #used for weighted average
                            total_rating += ratingdict[row[column_header]]
                            num_rows += 1
                       
                        average = total_rating / num_rows
                        #average = weighted_avg(total_rating)
                        rounded = round(average, 3)
                        if 2 <= rounded <= 2.99:
                            #print("bucket1")
                            bucket1.append(substring)
                        elif 3 <= rounded <= 3.99:
                            #print("bucket2")
                            bucket2.append(substring)
                        elif 4 <= rounded <= 5:
                            #print("bucket3")
                            bucket3.append(substring)
                        directory_path = "C:/Users/TEST/targetted_gt/raw_groundtruth/" + subject_name
                        try:
                            os.makedirs(directory_path)
                        except FileExistsError:
                            # directory already exists
                            pass
                        gt_output_bucket1_txt = "C:/Users/TEST/targetted_gt/raw_groundtruth/" + subject_name + "/Neutral.txt"
                        gt_output_bucket2_txt = "C:/Users/TEST/targetted_gt/raw_groundtruth/" + subject_name + "/Slightly.txt"
                        gt_output_bucket3_txt = "C:/Users/TEST/targetted_gt/raw_groundtruth/" + subject_name + "/Strongly.txt"
                        output = "C:/Users/rober/TEST/targetted_gt/raw_groundtruth/" + subject_name + "/num_failures.txt"
                        final_gt = bucket1 + bucket2 + bucket3
                        write_gt_to_txt(final_gt, output)
                        write_gt_to_txt(bucket1, gt_output_bucket1_txt)
                        write_gt_to_txt(bucket2, gt_output_bucket2_txt)
                        write_gt_to_txt(bucket3, gt_output_bucket3_txt)

                        #exit(0)
                        results[substring] = rounded
                    writw_to_csv(results, output_csv)

                except Exception as e:
                    print("Error: " + os.path.basename(subdir))
                    print(e)

#Different ways to calculate your ground truth from your N amount of ratings per keyboard navigation sequence
def median(nums):
    nums = sorted(nums)
    middle1 = (len(nums) - 1) // 2
    middle2 = len(nums) // 2
    return (nums[middle1] + nums[middle2]) / 2

#Different ways to calculate your ground truth from your N amount of ratings per keyboard navigation sequence
def weighted_avg(nums):
    dict = {"1": 0, "2": 0, "3": 0, "4": 0, "5": 0,}
    for num in nums:
        new_count = dict[str(num)]
        new_count += 1
        dict[str(num)] = new_count

    weighted_sum = ((dict["1"] * 1) + (dict["2"] * 2) + (dict["3"] * 3) + (dict["4"] * 4) + (dict["5"] * 5)) / len(nums)
    return weighted_sum

#This file is used to create the groundtruth from all of the results from the various google form LNFs surveys
if __name__ == '__main__':
    root_folder = '/Users/TEST/'  # Replace with the path to your root folder
    output_csv = '/Users/TEST/targetted_gt.csv'  # Output CSV file path
    process_xlsx_files_in_subfolders(root_folder, output_csv)




