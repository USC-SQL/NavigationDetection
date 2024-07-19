package edu.usc.WinnTree;

import java.util.ArrayList;
import java.util.List;

public class FunctionalArea {

    public List<Integer> MBR;
    public String xpath;
    public String ID;
    public String parent_id;
    public List<FunctionalArea> children;

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

}
