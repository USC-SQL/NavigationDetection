import bs4
from bs4 import BeautifulSoup
from bs4.formatter import HTMLFormatter
import pandas as pd
import re
#from xpath_soup import *
from urllib.request import urlopen
import utils
from pathlib import Path


class UnsortedAttributes(HTMLFormatter):
    def attributes(self, tag):
        for k, v in tag.attrs.items():
            yield k, v

def get_element(node):
    # for XPATH we have to count only for nodes with same type!
    prev_sib = node.find_previous_siblings(node.name)
    size = len(prev_sib)
    for s in prev_sib:
        s_class = get_element_class(s)

        if s_class and 'wave5icon' in s_class[0].strip():
            size = size - 1
    after_next = node.find_next_siblings(node.name)
    if len(prev_sib) == 0 and len(after_next) > 0:
        size = 1
    else:
        size + 1

    if size >= 1:
        return '%s:nth-of-type(%s)' % (node.name, size)
    else:
        return node.name


def get_css_path(node):
    path = [get_element(node)]
    for parent in node.parents:
        if parent.name == '[document]':
            break
        path.insert(0, get_element(parent))
    return ' > '.join(path)


def format_xpath(unformatted_xpath):
    elem = unformatted_xpath
    prev = ""
    if " > " in unformatted_xpath:
        elem = unformatted_xpath.split(" > ")
        xpath = ""
        if len(elem) > 1:
            for e in elem:
                if ':nth-child' in e:
                    e = e.replace(':nth-child', '')
                    e = e.replace(':nth-of-type', '')
                    e = e.replace('(', '[')
                    e = e.replace(')', ']')
                elif ':nth-of-type' in e:
                    e = e.replace(':nth-of-type', '')
                    e = e.replace('(', '[')
                    e = e.replace(')', ']')
                else:
                    e = e.strip()
                    e = e + '[1]'
                #Following snippet only applies if you forgot to delete the inserted div by WAVE at the top of the web page
                #prior to saving the web page with the WAVE results
                #if("body" in prev and "div" in e):
                #    if(e[-2] != "1"):
                #        value = int(e[-2])
                #        new_value = value - 1
                #        e = e.replace(str(value), str(new_value))
                xpath = xpath + "/" + e
                prev = e
        else:
            xpath = elem
    else:
        xpath = elem
    return xpath

def get_element_class(child):
    try:
        class_name = child["class"]
    except:
        return None
    return class_name


def process_subjects(list_subjects, path_to_subjects, path_to_processed_results):
    for subject in list_subjects:
        subject_problem_xpaths = set()
        subject_WAVE_site_path = path_to_subjects + "/WAVE/" + subject + "/index.html"
        try:
            page = open(subject_WAVE_site_path, 'rb')
            soup = BeautifulSoup(page.read(), 'html.parser')
            soup.prettify()
        except:
            print("Error: could not find WAVE output for " + subject)
            continue

        #df = pd.DataFrame(columns=['ACT', 'Description', 'xpath', 'element'])
        df = pd.DataFrame(columns=['Problem Xpaths'])


        alt_array = {}
        alt_array = [
            ["ALERTS: Tabindex", "2.4.3"]]
        for alt, act_no in alt_array:
            for element in soup.findAll("img", {"alt": alt}, recursive=True):
                new_el = element
                tabindex = None
                while True:
                    el = new_el.findPrevious()
                    s_class = get_element_class(el)
                    if not el or (s_class and "wave" in s_class[0].lower() and not 'wave5icon' in s_class[0].lower()):
                        #print('inside first if')
                        el = element.find_parent()
                        break
                    elif s_class and 'wave' in s_class[0].lower():
                        #el=el.find_parent()

                        try:
                            alt_t = el['alt']
                            #print('inside try')
                            if alt.lower().strip() == alt_t.lower().strip():
                                el = element.find_parent()
                                break
                        except:  # find other element
                            #print('inside except')
                            continue
                        #     print("xx")
                        new_el = el
                        continue
                    else:
                        if "tabindex" in alt.lower():
                            try:
                                tabindex = el['tabindex']
                            except:  # find other element
                                #print('inside except2')
                                new_el = el
                                continue
                        break
                css_selector = get_css_path(el)
                print(css_selector)
                xpath = format_xpath(css_selector)
                print(xpath)
                st_el = str(el.encode(formatter=UnsortedAttributes()).decode('utf-8'))
                print(st_el)
                print()
                #df.loc[len(df)] = [str(xpath)]
                subject_problem_xpaths.add(str(xpath))

        res = True

        subject_processed_path = path_to_processed_results + "/WAVE/" + subject + "/output.csv"
        subdir_path = path_to_processed_results + "/WAVE/" + subject
        Path(subdir_path).mkdir(parents=True, exist_ok=True)
        with open(subject_processed_path, "w") as file:
            for xpath in subject_problem_xpaths:
                file.write(xpath + "\n")
        

#Used to process WAVE's raw results
configs = {}
if __name__ == '__main__':
    configs = utils.get_config()
    subjects = utils.get_subjects(configs['path_to_subjects'])
    process_subjects(subjects, configs['path_to_raw_related_results'], configs['path_to_processed_related_results'])