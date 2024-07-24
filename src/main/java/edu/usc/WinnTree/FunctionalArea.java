package edu.usc.WinnTree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FunctionalArea {

    public List<Integer> MBR;
    public String xpath;
    public String ID;
    public String parent_id;
    public List<FunctionalArea> children;

    //Used for Affinity Calculations
    public List<Float> centroid;
    public Map<String, String> thematic_info;
    public HashSet applied_css;

    public FunctionalArea(String ID, String xpath, String parent_id, List<Integer> MBR) {
        this.MBR = MBR;
        this.xpath = xpath;
        this.parent_id = parent_id;
        this.ID = ID;
        this.children = new ArrayList<>();
    }

    public List<Integer> getMBR() {
        return MBR;
    }

    public void setMBR(List<Integer> MBR) {
        this.MBR = MBR;
    }

    public String getxpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getParentID() {
        return parent_id;
    }

    public void setParent(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public List<FunctionalArea> getChildren() {
        return children;
    }

    public void addChild(FunctionalArea child) {
        this.children.add(child);
    }

    ////////////////////////
    //
    //The following are used for affinity score calculation
    //
    /////////////////////////

    public void setCentroid( List<Float> centroid) {
        this.centroid = centroid;
    }
    public List<Float> getCentroid() {
        return centroid;
    };

    public void setThematic_info(Map<String, String> thematic_info) {
        this.thematic_info = thematic_info;
    }
    public Map<String, String> getThematic_info() {
        return thematic_info;
    };

    public void setApplied_css(HashSet applied_css) {
        this.applied_css = applied_css;
    }
    public HashSet getApplied_css() {
        return applied_css;
    };

}
