import os
import pandas as pd
import openpyxl
import csv


ratingdict = {
  "Strongly Disagree": 1,
  "Slightly Disagree": 2,
  "Neutral": 3,
  "Slightly Agree": 4,
  "Strongly Agree": 5
}

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
                    # Read the .xlsx file
                    df = pd.read_excel(file_path, engine = 'openpyxl')
                    results = {}
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
                        num_rows = 0
                        for index, row in df.iterrows():
                            total_rating += ratingdict[row[column_header]]
                            num_rows += 1

                        average = total_rating / num_rows
                        rounded = round(average, 3)
                        results[substring] = rounded
                    writw_to_csv(results, output_csv)
                except Exception as e:
                    print("Error: " + os.path.basename(subdir))
                    print(e)

    print(f"Results saved to {output_csv}")

#This file is used to create a CSV that showcases all of the results from the various google form LNFs surveys
if __name__ == '__main__':
    root_folder = '/Users/TEST'  # Replace with the path to your root folder
    output_csv = '/Users/TEST/validation_results_min.csv'  # Output CSV file path
    process_xlsx_files_in_subfolders(root_folder, output_csv)




