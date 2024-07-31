package edu.usc.Detection.DetectionHeuristics;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;

import javax.sound.midi.SysexMessage;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AlreadyVisited {

    public static boolean CheckVisited(WinnTree Wtree, UIGraphEdge edge){
        String source_xpath = edge.getV1().getXpath();
        String target_xpath = edge.getV2().getXpath();
        FunctionalArea source_FA = Wtree.FindByXpath(source_xpath);
        FunctionalArea target_FA = Wtree.FindByXpath(target_xpath);

        List<FunctionalArea> source_path = Wtree.FindDFSPath(source_FA);
        Collections.reverse(source_path);
        List<FunctionalArea> target_path = Wtree.FindDFSPath(target_FA);
        Collections.reverse(target_path);
        for(FunctionalArea source_iter: source_path){
            for(FunctionalArea target_iter: target_path){
                if(source_iter.equals(target_iter)){
                    //found LCA here
                    //Using LCA, we find the child containing source and check if its been fully explored
                    int index = source_path.indexOf(source_iter);
                    index -= 1;
                    FunctionalArea check = source_path.get(index);
                    if(Wtree.explored_vertices.contains(check)){
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
