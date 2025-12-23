package edu.usc.ContextTree.Construction;

import edu.usc.ContextTree.FunctionalArea;

import java.util.*;

import static edu.usc.ContextTree.Construction.GroupComparison.CheckMBR.CalculauteMBR;

public class ConstructParent {

    public static FunctionalArea GetNewParent(Set<FunctionalArea> children, int count){
        List<Integer> parent_mbr = CalculauteMBR(children);
        FunctionalArea parent = new FunctionalArea(String.valueOf(count), "", "", parent_mbr);
        Map<String, String> parent_thematic = new HashMap();
        String parent_theme_id = "";
        String parent_name = "";
        String parent_text = "";
        String all_thematic = "";
        List<String> find_background = new ArrayList<>();
        HashSet parent_css = new HashSet();
        for(FunctionalArea child: children){
            child.setParent(String.valueOf(count));
            if(parent_css.isEmpty()){
                parent_css = child.getApplied_css();
            } else {
                parent_css.retainAll(child.getApplied_css());
            }
            Map<String, String> child_thematic = child.getThematic_info();
            parent_theme_id += child_thematic.get("id") + " ";
            parent_name += child_thematic.get("name") + " ";
            parent_text += child_thematic.get("text") + " ";
            all_thematic += child.getAllThematic_info() + " ";
            find_background.add(child.getBackgroundColor());
            parent.addChild(child);
        }
        boolean allEqual = find_background.stream().distinct().limit(2).count() <= 1;
        parent_thematic.put("id", parent_theme_id);
        parent_thematic.put("name", parent_name);
        parent_thematic.put("text", parent_text);
        double midX = (parent_mbr.get(0) + parent_mbr.get(2))/2;
        double midY = (parent_mbr.get(1) + parent_mbr.get(3))/2;
        List<Double> centroid = new ArrayList<Double>();
        centroid.add(midX);
        centroid.add(midY);

        parent.setApplied_css(parent_css);
        parent.setThematic_info(parent_thematic);
        parent.setCentroid(centroid);
        parent.setAllThematic_info(all_thematic);
        if(allEqual){
            parent.setBackgroundColor(find_background.get(0));
        } else {
            parent.setBackgroundColor("");
        }
        return parent;
    }

}
