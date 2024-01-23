# Keyboard Navigation Failure Detection

This project encompasses the automatic detection of keyboard-based navigation issues in web pages.

### BonaGarcia Test Instructions

To test the validity of the BG setup, please do the following:
1. run the ``main`` method in the ``ChromeTest.java`` class

If successful, this will open an active webdriver and navigate to the Bonigarcia Java webpage. 

### Clustering Test Instructions

To cluster the visible elements of an input webpage, please do the following:
1. Specify URL of the subject webpage in the ``config.txt`` file under the ``subject_live`` attribute
2. Run the ``edu.usc.Main.java`` class

If successful (as of 1/23/2024), this will produce an output of a feature vector for all visible elements of the input webpage.
