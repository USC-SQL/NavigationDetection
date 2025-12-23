package edu.usc.Detection;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.Utilities.LoadConfig;
import edu.usc.ContextTree.ContextTree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import static edu.usc.Detection.DetectionHeuristics.DetectFailures.FindFailures;
import static edu.usc.KFG.KFGUtilities.UtilityFunctions.LoadTheKFG;
import static edu.usc.Utilities.SaveFiles.SaveXLSX;

public class RunDetection {

    public static void Detect(LoadConfig configs_obj, String subject) throws Exception {
        long startTime = System.nanoTime();

        UIGraphState KFG = LoadTheKFG(configs_obj, subject);
        ContextTree CTree = new ContextTree();
        CTree.Load(configs_obj, subject);
        Set<UIGraphEdge> LNF_failures = FindFailures(CTree, KFG, configs_obj, subject);
        SaveDetectedFailures(LNF_failures, configs_obj, subject, "DetectedLNFs.txt");
        long endTime = System.nanoTime();
        long duration = ((endTime - startTime) / 1000000) / 1000;
        System.out.println("Detection has completed.");
        System.out.println("Detection Timing (seconds): " + duration);
        SaveXLSX(configs_obj, subject, duration, "Detection");
        System.out.println("LNFs: " + LNF_failures.size());
    }

    public static void SaveDetectedFailures(Set<UIGraphEdge> LNF_failures, LoadConfig configs_obj, String subject, String FileName) throws IOException {
        String path = configs_obj.getProperties().getProperty("Detection_output_location") + File.separator + subject + File.separator + FileName;

        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        for(UIGraphEdge edge: LNF_failures){
            String converted_edge = edge.getV1().getXpath() + " - > " + edge.getV2().getXpath();
            if(edge.getVar().equals("entry")){
                converted_edge = "Browser - > " + edge.getV1().getXpath();
            }
            if(edge.getVar().equals("exit")){
                converted_edge = edge.getV2().getXpath() + " - > Browser";
            }
            writer.write(converted_edge);
            writer.newLine();
        }
        writer.close();
        System.out.println(FileName + " has been saved to file.");
    }

}
