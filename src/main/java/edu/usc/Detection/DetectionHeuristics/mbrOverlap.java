package edu.usc.Detection.DetectionHeuristics;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphNode;
import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.usc.WinnTree.Construction.GroupComparison.CheckMBR.FindMBR;
import static edu.usc.WinnTree.Construction.GroupComparison.CheckMBR.OverlappingMBR;

public class mbrOverlap {

    public static boolean MBRFailure(WinnTree Wtree, UIGraphEdge edge){
        String source_xpath = edge.getV1().getXpath();
        String target_xpath = edge.getV2().getXpath();
        FunctionalArea source_FA = Wtree.FindByXpath(source_xpath);
        FunctionalArea target_FA = Wtree.FindByXpath(target_xpath);
        Set Edge_group = new HashSet<>();
        Edge_group.add(source_FA);
        Edge_group.add(target_FA);
        List Edge_mbr = FindMBR(Edge_group);
        for(FunctionalArea FA: Wtree.getVertexSet()){
            List FA_mbr = FA.getMBR();
            if(!FA.getxpath().equals("") && !FA.getxpath().equals(source_xpath) && !FA.getxpath().equals(target_xpath)) {
                if(OverlappingMBR(Edge_mbr, FA_mbr)) {
                    return true;
                }
            }
        }
        return false;
    }

}
