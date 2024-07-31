package edu.usc.Detection.DetectionHeuristics;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.usc.Detection.DetectionHeuristics.AlreadyVisited.CheckVisited;
import static edu.usc.Detection.DetectionHeuristics.mbrOverlap.MBRFailure;

public class DetectFailures {

    public static void FindFailures(WinnTree Wtree, UIGraphState KFG){
        Set<UIGraphEdge> problematic_edges = new HashSet<>();
        List<UIGraphEdge> nav_order = KFG.getOrder(); //we trace the KFG to find failure edges

        FunctionalArea start = Wtree.FindByXpath(KFG.getV_entry().getXpath());
        Wtree.AddExplored(start);
        for(UIGraphEdge edge: nav_order){
            if(MBRFailure(Wtree, edge)){
                problematic_edges.add(edge);
            }
            if(CheckVisited(Wtree, edge)){
                problematic_edges.add(edge);
            }
            Wtree.AddExplored(Wtree.FindByXpath(edge.getV2().getXpath())); //adds the target node to W-tree explored set
        }
        System.out.println(problematic_edges.size());
    }

}
