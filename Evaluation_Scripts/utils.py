import csv
import os

def get_subjects(subjects_file):
    with open(subjects_file) as file:
        all_subjects = [line.rstrip() for line in file]
    return all_subjects

def get_config():
    res = {}
    path = "./config.txt"
    with open(path) as file:
        for line in file:
            if line[0] == "#" or line[0] == '\n':
                continue
            key_value = line.split("=")

            if len(key_value) == 2:
                res[key_value[0]] = key_value[1].strip()
            else:
                print("There was a problem when parsing config.txt for line:", line)
    #print("[DEBUG] loaded configs:", res)
    return res


if __name__ == "__main__":
    print("Running from " + os.getcwd())
    configs = get_config()
    print("Configs are: " + configs)
    if(len(configs) != 0):
        print("Success")
    else:
        print("Failure")