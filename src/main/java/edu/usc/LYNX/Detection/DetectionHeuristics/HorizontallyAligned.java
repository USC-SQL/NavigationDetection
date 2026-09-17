package edu.usc.LYNX.Detection.DetectionHeuristics;

import edu.usc.LYNX.ContextTree.FunctionalArea;
import edu.usc.LYNX.ContextTree.ContextTree;

import java.util.ArrayList;
import java.util.List;

import static edu.usc.LYNX.Helper.Detection.HeuristicUtility.EdgeCentroidAligned;
import static edu.usc.LYNX.Helper.Detection.HeuristicUtility.EdgeHorizontallyAligned;

public class HorizontallyAligned {

    public static boolean CheckHorizontallyAligned(ContextTree Ctree, List<FunctionalArea> source_path, List<FunctionalArea> target_path){
        FunctionalArea LCA = new FunctionalArea("", "", "", new ArrayList<>());
        for(FunctionalArea source_iter: source_path){
            for(FunctionalArea target_iter: target_path){
                if(source_iter.equals(target_iter)){
                    LCA = source_iter;
                    break;
                }
            }
            if(!LCA.getID().equals("")){
                break;
            }
        }
        for(FunctionalArea source_iter: source_path){
            if(source_iter.equals(LCA)){
                break;
            }
            for(FunctionalArea target_iter: target_path){
                if(target_iter.equals(LCA)){
                    break;
                }
                boolean check = EdgeHorizontallyAligned(source_iter, target_iter);
                boolean check2 = EdgeCentroidAligned(source_iter, target_iter);
                if (check || check2) {
                    if(CheckBackwardsFailure(source_iter, target_iter)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean CheckBackwardsFailure(FunctionalArea source, FunctionalArea target){
        int source_x = source.getMBR().get(0);
        int target_x = target.getMBR().get(0);
        //checks to see if source FA is on right side of target (ie a backwards edge)
        //the +10 helps avoid super whacky edge case
        if(source_x > target_x + 10){
            return true;
        }
        return false;
    }

}
