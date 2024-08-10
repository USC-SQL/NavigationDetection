package edu.usc.Detection.DetectionHeuristics;

import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;

import java.util.ArrayList;
import java.util.List;

public class HorizontallyAligned {

    public static boolean CheckHorizontallyAligned(WinnTree Wtree, List<FunctionalArea> source_path, List<FunctionalArea> target_path){
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
                boolean check = Wtree.IsHorizontallyAligned(source_iter, target_iter);
                if (check) {
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
        if(source_x > target_x){
            return true;
        }
        return false;
    }

}
