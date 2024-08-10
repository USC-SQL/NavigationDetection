package edu.usc.Detection;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.Utilities.LoadConfig;
import edu.usc.WinnTree.WinnTree;

import java.io.IOException;
import java.util.Set;

import static edu.usc.Detection.DetectionHeuristics.DetectFailures.FindFailures;
import static edu.usc.Detection.FinalLocalization.FindFaultyElements;
import static edu.usc.Detection.LocalizationHeuristics.LocalizeFailures.Localize;
import static edu.usc.KFG.PrepareKFG.LoadTheKFG;

public class RunDetection {

    public static void Detect(LoadConfig configs_obj, String subject) throws Exception {
        long startTime = System.nanoTime();

        UIGraphState KFG = LoadTheKFG(configs_obj, subject);
        WinnTree WTree = new WinnTree();
        WTree.Load(configs_obj, subject);
        Set<UIGraphEdge> LNF_failures = FindFailures(WTree, KFG);
        Set<UIGraphEdge> Localized_LNF_failures = Localize(WTree, KFG, LNF_failures);
        System.out.println("Localized size: " + Localized_LNF_failures.size());
        FindFaultyElements(Localized_LNF_failures, configs_obj, subject);
        long endTime = System.nanoTime();
        long duration = ((endTime - startTime) / 1000000) / 1000;
        System.out.println("Timing: " + duration);
    }

}
