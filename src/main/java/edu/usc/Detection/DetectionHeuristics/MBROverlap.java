package edu.usc.Detection.DetectionHeuristics;

import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.usc.WinnTree.Construction.GroupComparison.CheckMBR.CalculauteMBR;
import static edu.usc.WinnTree.Construction.GroupComparison.CheckMBR.OverlappingMBR;

public class MBROverlap {

    public static boolean MBRFailure(WinnTree Wtree, String source_xpath, String target_xpath, FunctionalArea source_FA, FunctionalArea target_FA){
        Set Edge_group = new HashSet<>();
        Edge_group.add(source_FA);
        Edge_group.add(target_FA);
        List Edge_mbr = CalculauteMBR(Edge_group);
        for(FunctionalArea FA: Wtree.getVertexSet()){
            List FA_mbr = FA.getMBR();
            if(!FA.getXpath().equals("") && !FA.getXpath().equals(source_xpath) && !FA.getXpath().equals(target_xpath)) {
                if(OverlappingMBR(Edge_mbr, FA_mbr)) {
                    return true;
                }
            }
        }
        return false;
    }

}
