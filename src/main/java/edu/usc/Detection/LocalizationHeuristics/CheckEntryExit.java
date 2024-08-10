package edu.usc.Detection.LocalizationHeuristics;

import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;

public class CheckEntryExit {

    public static boolean CheckEntryEdge(WinnTree Wtree, FunctionalArea start){
        int start_y = start.getMBR().get(1);
        for(FunctionalArea FA: Wtree.getVertexSet()){
            if(!FA.getXpath().equals("") && !FA.equals(start)){
                int FA_y = FA.getMBR().get(1);
                if(FA_y < start_y){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean CheckExitEdge(WinnTree Wtree, FunctionalArea exit){
        int exit_y = exit.getMBR().get(1);
        for(FunctionalArea FA: Wtree.getVertexSet()){
            if(!FA.getXpath().equals("") && !FA.equals(exit)){
                int FA_y = FA.getMBR().get(1);
                if(FA_y > exit_y){
                    return true;
                }
            }
        }
        return false;
    }

}
