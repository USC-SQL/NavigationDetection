package edu.usc.Detection;

import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.Utilities.LoadConfig;
import edu.usc.WinnTree.WinnTree;

import java.io.IOException;

import static edu.usc.Detection.DetectionHeuristics.DetectFailures.FindFailures;
import static edu.usc.KFG.PrepareKFG.LoadTheKFG;

public class RunDetection {

    public static void Test(LoadConfig configs_obj, String subject) throws IOException {
        UIGraphState KFG = LoadTheKFG(configs_obj, subject);
        WinnTree WTree = new WinnTree();
        WTree.Load(configs_obj, subject);
        FindFailures(WTree, KFG);
    }

}
