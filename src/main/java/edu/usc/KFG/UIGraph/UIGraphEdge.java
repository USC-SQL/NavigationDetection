package edu.usc.KFG.UIGraph;

public class UIGraphEdge implements Comparable<UIGraphEdge> {

    protected UIGraphNode v1;
    protected UIGraphNode v2;
    protected String var;

    public UIGraphEdge() {

    }

    public UIGraphEdge(UIGraphNode v1, UIGraphNode v2, String var) {
        // TODO Auto-generated constructor stub
        this.v1 = v1;
        this.v2 = v2;
        this.var = var;
    }

    public UIGraphNode getV1() {
        return v1;
    }

    public UIGraphNode getV2() {
        return v2;
    }

    public String getVar() {
        return var;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof UIGraphEdge)) {
            return false;
        }

        UIGraphEdge uiGraphEdge = (UIGraphEdge) o;

        return uiGraphEdge.getV1().equals(v1) &&
                uiGraphEdge.getV2().equals(v2) &&
                uiGraphEdge.getVar().equals(var);
    }

    //Idea from effective Java : Item 9
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + v1.hashCode();
        result = 31 * result + v2.hashCode();
        result = 31 * result + var.hashCode();
        return result;
    }


    @Override
    public String toString() {
        return "[" + v1 + ",  " + v2 + ",  " + var + "]";
    }


    @Override
    public int compareTo(UIGraphEdge edge2) {
        return v1.getXpath().compareTo(edge2.getV1().getXpath());
    }

}