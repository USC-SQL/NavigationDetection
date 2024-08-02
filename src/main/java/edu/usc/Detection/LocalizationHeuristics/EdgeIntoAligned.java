package edu.usc.Detection.LocalizationHeuristics;

import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EdgeIntoAligned {

    public static boolean CheckEdgeIntoAligned(WinnTree Wtree, List<FunctionalArea> source_path, List<FunctionalArea> target_path){
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
                    if(Wtree.IsHorizontallyAligned(child, other_children)){
                        int child_x = child.getMBR().get(0);
                        int other_child_x = other_children.getMBR().get(0);
                        if(child_x > other_child_x){
                            return true;
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
