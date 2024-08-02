package edu.usc.Detection.LocalizationHeuristics;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphNode;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.usc.Detection.DetectionHeuristics.AlreadyVisited.CheckVisited;
import static edu.usc.Detection.DetectionHeuristics.HorizontallyAligned.CheckHorizontallyAligned;
import static edu.usc.Detection.LocalizationHeuristics.CheckEntryExit.CheckEntryEdge;
import static edu.usc.Detection.LocalizationHeuristics.CheckEntryExit.CheckExitEdge;
import static edu.usc.Detection.LocalizationHeuristics.EdgeIntoAligned.CheckEdgeIntoAligned;

public class LocalizeFailures {

    public static void Localize(WinnTree Wtree, UIGraphState KFG, Set<UIGraphEdge> LNF_failures){
        Set<UIGraphEdge> localized_edges = new HashSet<>();
        List<UIGraphEdge> nav_order = KFG.getOrder(); //we trace the KFG to find failure edges
        Wtree.resetExploredSet();
        FunctionalArea end = Wtree.FindByXpath(KFG.getV_exit().getXpath());
        FunctionalArea start = Wtree.FindByXpath(KFG.getV_entry().getXpath());
        Wtree.AddExplored(start);
        for(UIGraphEdge edge: nav_order){
            if(LNF_failures.contains(edge)){
                String source_xpath = edge.getV1().getXpath();
                String target_xpath = edge.getV2().getXpath();
                FunctionalArea source_FA = Wtree.FindByXpath(source_xpath);
                FunctionalArea target_FA = Wtree.FindByXpath(target_xpath);
                //Find ancestry of source and target nodes
                List<FunctionalArea> source_path = Wtree.FindDFSPath(source_FA);
                Collections.reverse(source_path);
                List<FunctionalArea> target_path = Wtree.FindDFSPath(target_FA);
                Collections.reverse(target_path);

                if(CheckVisited(Wtree, source_path, target_path)){
                    localized_edges.add(edge);
                }
                if(CheckHorizontallyAligned(Wtree, source_path, target_path)){
                    localized_edges.add(edge);
                }
                if(CheckEdgeIntoAligned(Wtree, source_path, target_path)){
                    localized_edges.add(edge);
                }
            }
            Wtree.AddExplored(Wtree.FindByXpath(edge.getV2().getXpath()));
        }
        if(CheckEntryEdge(Wtree, start)){
            UIGraphEdge edge = new UIGraphEdge(KFG.getV_entry(), KFG.getV_entry(), "entry");
            localized_edges.add(edge);
        }
        if(CheckExitEdge(Wtree, end)){
            UIGraphEdge edge = new UIGraphEdge(KFG.getV_exit(), KFG.getV_exit(), "exit");
            localized_edges.add(edge);
        }
        System.out.println(localized_edges.size());
    }

}
