package edu.usc.Detection.DetectionHeuristics;

import edu.usc.ContextTree.FunctionalArea;
import edu.usc.ContextTree.ContextTree;
import java.util.List;

public class LeftPrematurely {

    public static boolean CheckAllVisited(ContextTree Ctree, List<FunctionalArea> source_path, List<FunctionalArea> target_path){
        for(FunctionalArea source_iter: source_path){
            for(FunctionalArea target_iter: target_path){
                if(source_iter.equals(target_iter)){
                    //found LCA here
                    //Using LCA, we find the child containing source and check if its been fully explored
                    int index = source_path.indexOf(source_iter);
                    index -= 1;
                    FunctionalArea check = source_path.get(index);
                    if(Ctree.fully_explored_vertices.contains(check)){
                        return false;
                    } else{
                        return true;
                    }

                }
            }
        }
        return false; //if done correctly this should never be reached
    }

}
