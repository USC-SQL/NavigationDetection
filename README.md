# Linear Navigation Failure (LNF) Detection

This project encompasses the automatic detection of keyboard-based navigation issues in web pages.

The experiments presented in this paper were run on the following systems/versions:

Windows 11

Chrome v126

Python 3.11.4 (Evaluation scripts)

Java SDK 17

The first step is to edit the ``config.txt`` file. Inside, you will be presented with several arguments. Please download the study’s associated data (available here) and update the config files with the associated paths relevant to your machine. Note that this project conducts its analysis on cached versions of webpages via the open-source tool called MITMproxy – at the moment, the analysis is not configured to be run on a live web page via an input URL.

The project is split into 3 distinct phases: KFG construction, C-Tree construction, and Evaluation.

# KFG construction
The KFG construction process is taken from related work, BAGEL by Chiou et al. As such, one should note that the automatically generated KFG’s are prone to errors. A new KFG can be generated after one has cached a webpage via mitmproxy.

# C-Tree construction
Once you have created a KFG (or are planning to utilize one of the ones we provide), then the next step is to create a C-Tree. Follow the steps below in their numerical order:
1.	In ``subjects.txt``, list the subject you would like to run (e.g., yumpu)
2.	In ``Main.txt``, comment out lines 25 and 34 – doing so will make lines 26-33 available to be run. Press run/compile
3.	If you run into any errors, please double-check your config file to ensure that the paths are pointing to the correct locations.
4.	Consecutive runs may cause the ‘mitmproxy’ to not close properly. Leaving a proxy open can cause future errors for subsequent experiments. To fix this issue, please check your system's Task Manager after each compilation to ensure that all programs related to ‘mitmproxy’ have been terminated. If they have not, then manually terminate them prior to the start of the next experiment.

# Evaluation
The first step of running our study’s evaluation begins in your IDE of choice to run the project’s Java code. Follow the steps below in their numerical order:
1.	In ``subjects.txt``, list the subject you would like to run (e.g., yumpu)
2.	In ``Main.txt``, comment out the LOC that were utilized during the KFG construction and/or the C-Tree construction, if applicable
3.	In ``Main.txt``, uncomment line 37 and press run/compile.
4.	If you run into any errors, please double-check your config file to ensure that the paths are pointing to the correct locations.

Following the steps listed above should provide you with the LNF detection output of LYNX for a given subject. The raw output is represented via a txt file that contains ‘failure’ edges from the KFG that were detected as LNFs. They are represented in the format of “source_xpath -> target_xpath”.

After acquiring the raw output of LYNX, navigate to the evaluation’s collection of Python scripts located within the project’s ``Evaluation_Scripts`` folder. To calculate precision and recall, perform the following:
1.	Update ``config.txt`` according to your personal machine
2.	Update ``subjects.txt`` to include which set of subjects you’d like to run the evaluation on. Each subject should be written individually on each line
3.	Either run ``LYNX_eval_script.py`` to acquire LYNX’s results or ``eval_related_tools.p`` to acquire the results from a related work. The output of both files will be an xslx file that details the respective tools’ performance on each subject included from Step 2.
4.	To change which related work you’d like to evaluate, edit Line 103 in ``eval_related_tools.py`` to the name of the selected related work (e.g., ``WAVE``).

