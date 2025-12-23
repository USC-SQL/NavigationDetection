package edu.usc.Detection.DetectionHeuristics;

import edu.usc.ContextTree.FunctionalArea;
import edu.usc.ContextTree.ContextTree;

import java.util.ArrayList;
import java.util.List;

import static edu.usc.ContextTree.Construction.GroupComparison.CheckMBR.OverlappingMBR;

public class CheckEntryExit {

    public static boolean CheckEntryEdge(ContextTree Ctree, FunctionalArea start){
        List<Integer> test_hidden_element = new ArrayList<>();
        test_hidden_element.add(0);
        test_hidden_element.add(0);
        test_hidden_element.add(0);
        test_hidden_element.add(0);
        List<Integer> Edge_mbr = new ArrayList<>();
        Edge_mbr.add(0);
        Edge_mbr.add(0);
        Edge_mbr.add(start.getMBR().get(2));
        Edge_mbr.add(start.getMBR().get(3));
        for(FunctionalArea FA: Ctree.getVertexSet()){
            List FA_mbr = FA.getMBR();
            //checks if the FA mbr is [0, 0, 0, 0]
            if(FA_mbr.equals(test_hidden_element)){
                FA_mbr.remove(3);
                FA_mbr.remove(2);
                FA_mbr.add(1);
                FA_mbr.add(1);
            }
            if(!FA.getXpath().equals("") && !FA.equals(start)){
                if(OverlappingMBR(Edge_mbr, FA_mbr)) {
                    return true;
                }
            }
        }

        /*
        int start_y = start.getMBR().get(1);
        for(FunctionalArea FA: Wtree.getVertexSet()){
            if(!FA.getXpath().equals("") && !FA.equals(start)){
                int FA_y = FA.getMBR().get(1);
                if(FA_y < start_y){
                    return true;
                }
            }
        }
         */
        return false;
    }

    public static boolean CheckExitEdge(ContextTree Ctree, FunctionalArea exit){
        List<Integer> Edge_mbr = new ArrayList<>();
        Edge_mbr.add(exit.getMBR().get(0));
        Edge_mbr.add(exit.getMBR().get(1));
        if(exit.getMBR().get(0) > 1280){
            Edge_mbr.add(exit.getMBR().get(0));
        } else {
            Edge_mbr.add(1280);
        }
        if(exit.getMBR().get(1) > 1024){
            Edge_mbr.add(exit.getMBR().get(1));
        } else {
            Edge_mbr.add(1024);
        }
        for(FunctionalArea FA: Ctree.getVertexSet()){
            List FA_mbr = FA.getMBR();
            if(!FA.getXpath().equals("") && !FA.equals(exit)){
                if(OverlappingMBR(Edge_mbr, FA_mbr)) {
                    return true;
                }
            }
        }
        /*
        int exit_y = exit.getMBR().get(1);
        for(FunctionalArea FA: Wtree.getVertexSet()){
            if(!FA.getXpath().equals("") && !FA.equals(exit)){
                int FA_y = FA.getMBR().get(1);
                if(FA_y > exit_y){
                    System.out.println(FA.getXpath());
                    return true;
                }
            }
        }
         */
        return false;
    }

}
