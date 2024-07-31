package edu.usc.Detection.DetectionHeuristics;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphNode;
import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.usc.WinnTree.Construction.GroupComparison.CheckMBR.FindMBR;
import static edu.usc.WinnTree.Construction.GroupComparison.CheckMBR.OverlappingMBR;

public class MBRHeuristic {

    public static boolean MBRFailure(UIGraphEdge edge, WinnTree Wtree){
        UIGraphNode vertexOne = edge.getV1();
        UIGraphNode vertexTwo = edge.getV2();
        String v1_xpath = vertexOne.getXpath();
        String v2_xpath = vertexTwo.getXpath();
        FunctionalArea vertexOne_FA = Wtree.FindByXpath(v1_xpath);
        FunctionalArea vertexTwo_FA = Wtree.FindByXpath(v2_xpath);
        Set Edge_group = new HashSet<>();
        Edge_group.add(vertexOne_FA);
        Edge_group.add(vertexTwo_FA);
        List Edge_mbr = FindMBR(Edge_group);
        for(FunctionalArea FA: Wtree.getVertexSet()){
            List FA_mbr = FA.getMBR();
            if(!FA.getxpath().equals("") && !FA.getxpath().equals(v1_xpath) && !FA.getxpath().equals(v2_xpath)) {
                if(OverlappingMBR(Edge_mbr, FA_mbr)) {
                    return true;
                }
            }
        }
        return false;
    }

}
