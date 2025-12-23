package edu.usc.Detection.DetectionHeuristics;

import edu.usc.ContextTree.FunctionalArea;
import edu.usc.ContextTree.ContextTree;

import java.util.ArrayList;
import java.util.List;

import static edu.usc.Utilities.Detection.HeuristicUtility.*;

public class VerticalAscent {

    public static boolean CheckVerticalAscent(ContextTree Ctree, List<FunctionalArea> source_path, List<FunctionalArea> target_path){
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
                if (EdgeVerticallyAligned(source_iter, target_iter)) {
                    if(CheckVerticalFailure(source_iter, target_iter)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean CheckVerticalFailure(FunctionalArea source, FunctionalArea target){
        int source_y = source.getMBR().get(1);
        int target_y = target.getMBR().get(1);

        if(source_y > target_y ){
            return true;
        }
        return false;
    }
}
