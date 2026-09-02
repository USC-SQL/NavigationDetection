import json
import os
import utils
import pandas as pd
from pathlib import Path

def format_xpath(unformatted_xpath):
    elem = unformatted_xpath
    if " > " in unformatted_xpath:
        elem = unformatted_xpath.split(" > ")
        xpath = ""
        if len(elem) > 1:
            for e in elem:
                if ':nth-child' in e:
                    e = e.replace(':nth-child', '')
                    e = e.replace('(', '[')
                    e = e.replace(')', ']')
                elif ':nth-of-type' in e:
                    e = e.replace(':nth-of-type', '')
                    e = e.replace('(', '[')
                    e = e.replace(')', ']')
                else:
                    e = e.strip()
                    e = e + '[1]'
                xpath = xpath + "/" + e
        else:
            xpath = elem
    else:
        xpath = elem
    return xpath

def process_subjects(list_subjects, path_to_subjects, path_to_processed_results):
    empty_json = {}    
    for subject in list_subjects:
        subject_problem_xpaths = set() #used to keep track of xpaths of all elements that were found to fail certain WCAG guidelines
        subject_json_data = json.dumps(empty_json)
        subject_path = path_to_subjects + "/QualWeb/" + subject + "/evaluation.json"
        subject_processed_path = path_to_processed_results + "/QualWeb/" + subject + "/output.csv"

        try:
            with open(subject_path, encoding='utf-8') as json_file:
                subject_json_data = json.load(json_file)
        except:
            print("Error: could not find Qualweb json file for " + subject)
            continue

        #These are all the rules that are related to WCAG 2.4.3 in some way shape or form
        subject_data = []
        subject_data.append(subject_json_data['html']['QW-WCAG-T6'])
        subject_data.append(subject_json_data['html']['QW-WCAG-T15'])
        subject_data.append(subject_json_data['html']['QW-WCAG-T24'])
        subject_data.append(subject_json_data['act']['QW-ACT-R13'])
        subject_data.append(subject_json_data['act']['QW-ACT-R74'])
        subject_data.append(subject_json_data['act']['QW-ACT-R70'])

        for item in subject_data:
            if(item['metadata']['warning'] != 0 or item['metadata']['failed'] != 0):
                for data in item['results']:
                    if(data['verdict'] != 'passed'):
                        for elements in data['elements']:
                            print(elements['pointer'])
                            xpath = format_xpath(elements['pointer'])
                            if(xpath != "html"):
                                subject_problem_xpaths.add(xpath)

        subdir_path = path_to_processed_results + "/QualWeb/" + subject
        Path(subdir_path).mkdir(parents=True, exist_ok=True)
        with open(subject_processed_path, "w") as file:
            for xpath in subject_problem_xpaths:
                file.write(xpath + "\n")


#Used to process Qualweb's raw results
configs = {}
if __name__ == "__main__":
    configs = utils.get_config()
    subjects = utils.get_subjects(configs['path_to_subjects'])
    process_subjects(subjects, configs['path_to_raw_related_results'], configs['path_to_processed_related_results'])