package edu.usc.Detection.DetectionHeuristics;

import edu.usc.ContextTree.FunctionalArea;
import edu.usc.ContextTree.ContextTree;

import java.util.ArrayList;
import java.util.List;

public class ReEntering {

    public static boolean CheckReEnter(ContextTree Ctree, List<FunctionalArea> source_path, List<FunctionalArea> target_path){
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

        for(FunctionalArea target_iter_two: target_path){
            if(target_iter_two.equals(LCA)){
                break;
            }
            if(Ctree.getVisitedSet().contains(target_iter_two)){
                return true;
            } else {
                Ctree.AddVisited(target_iter_two);
            }
        }
        return false;
    }

}
