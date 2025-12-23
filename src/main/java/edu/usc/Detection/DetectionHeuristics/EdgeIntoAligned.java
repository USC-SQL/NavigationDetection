package edu.usc.Detection.DetectionHeuristics;

import edu.usc.ContextTree.FunctionalArea;
import edu.usc.ContextTree.ContextTree;

import java.util.ArrayList;
import java.util.List;

import static edu.usc.Utilities.Detection.HeuristicUtility.EdgeCentroidAligned;
import static edu.usc.Utilities.Detection.HeuristicUtility.EdgeHorizontallyAligned;

public class EdgeIntoAligned {

    public static boolean CheckEdgeIntoAligned(ContextTree Ctree, List<FunctionalArea> source_path, List<FunctionalArea> target_path){
        FunctionalArea source = source_path.get(0);
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
        for(FunctionalArea target: target_path){
            if(target.getChildren().isEmpty()){
                continue;
            } else{
                int index = target_path.indexOf(target);
                index -= 1;
                FunctionalArea child = target_path.get(index);
                for(FunctionalArea other_children: target.getChildren()){
                    if(EdgeHorizontallyAligned(child, other_children) || EdgeCentroidAligned(child, other_children)) {
                        int child_x = child.getMBR().get(0);
                        int other_child_x = other_children.getMBR().get(0);
                        if (child_x > other_child_x && !Ctree.getFullyExploredSet().contains(other_children)) {
                            if(source.getMBR().get(3) < child.getMBR().get(1)) {
                                return true;
                            }
                        }
                    }

                }
            }
            //we put it after the forloop just in case the source and target of the edge are in the same FA
            if (target.equals(LCA)) {
                break;
            }
        }
        return false;
    }

}
