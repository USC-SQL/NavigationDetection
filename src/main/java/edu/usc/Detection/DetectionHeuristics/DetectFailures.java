package edu.usc.Detection.DetectionHeuristics;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphNode;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.WinnTree.WinnTree;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.usc.Detection.DetectionHeuristics.MBRHeuristic.MBRFailure;

public class DetectLNFFailures {

    public static void FindFailures(WinnTree Wtree, UIGraphState KFG){
        Set<UIGraphEdge> problematic_edges = new HashSet<>();
        List<UIGraphEdge> nav_order = KFG.getOrder();
        for(UIGraphEdge edge: nav_order){
            if(MBRFailure(edge, Wtree)){
                problematic_edges.add(edge);
            }
        }
        System.out.println(problematic_edges.size());
    }

}
