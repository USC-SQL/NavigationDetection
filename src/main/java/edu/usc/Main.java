package edu.usc;

import java.util.*;

import edu.usc.Utilities.LoadConfig;
import edu.usc.ContextTree.ContextTree;

import static edu.usc.Detection.RunDetection.Detect;
import static edu.usc.Utilities.ContextTreeValidation.DrawContextTreeProxy.DrawMBRs;
import static edu.usc.Utilities.LNFValidation.DrawLNFLabels.DrawLabels;
import static edu.usc.Utilities.LNFValidation.ReviewFN.PrintFNs;
import static edu.usc.Utilities.SaveFiles.SaveXLSX;


public class Main {
    public static void main(String[] args) throws Exception {
        LoadConfig configs_obj = new LoadConfig();
        List<String> subjects = configs_obj.getSubjects();
        for(String subject: subjects) {
            System.out.println("Subject: " + subject);
            System.out.println("Subject URL: " + configs_obj.getSubjectURL(subject));
            //UIGraphState KFG = LoadTheKFG(configs_obj, subject);

            //C-Tree CONSTRUCTION
            /*
            long startTime = System.nanoTime();
            ContextTree Tree = new ContextTree();
            Tree.Build(configs_obj, subject);
            Tree.Save(configs_obj, subject);
            long endTime = System.nanoTime();
            long duration = ((endTime - startTime) / 1000000 / 1000);
            System.out.println("C-Tree Timing: " + duration);
            SaveXLSX(configs_obj, subject, duration, "CTree");
             */

            //LNF DETECTION
            //Detect(configs_obj, subject); //used for LNF detection

            //Auxiliary/additional functions
            //DrawMBRs(subject, configs_obj); //used to draw MBRs of CTree for a subject
            //DrawLabels(subject, configs_obj); //used to draw labels of all elements in a subjects (via KFG)
            //PrintFNs(subject, configs_obj); //used to print out all FN's after LNF detection has occured and processed via python
            /*
            //Below scripts are used to in the conversion process for building your own GT after the python script
            ConvertGTXpaths(subject, configs_obj, "Neutral.txt");
            ConvertGTXpaths(subject, configs_obj, "Slightly.txt");
            ConvertGTXpaths(subject, configs_obj, "Strongly.txt");
            ConvertGTXpaths(subject, configs_obj, "num_failures.txt");
            */
        }
        System.exit(0);
    }
}