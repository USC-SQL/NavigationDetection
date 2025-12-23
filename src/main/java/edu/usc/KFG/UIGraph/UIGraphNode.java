package edu.usc.KFG.UIGraph;

import java.util.HashMap;
import java.util.Map;

public class UIGraphNode implements Comparable<UIGraphNode> {

    protected String xpath;
    int x, y, width, height;

    int order;

    public UIGraphNode() {

    }

    public UIGraphNode(String xpath) {
        // TODO Auto-generated constructor stub
        this.xpath = xpath;
    }

    public String getXpath() {
        return xpath;
    }


    public void setOrder(int order){this.order = order;}
    public int getOrder(){return order;}

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof UIGraphNode)) {
            return false;
        }

        UIGraphNode knfgnode = (UIGraphNode) o;

        return knfgnode.getXpath().equals(xpath);
    }

    //Idea from effective Java : Item 9
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + xpath.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[" + xpath + "]";
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub

        UIGraphNode v1 = new UIGraphNode("kk");
        UIGraphNode v2 = new UIGraphNode("kk");

        System.out.println(v1.equals(v2));

        Map<UIGraphNode, String> map = new HashMap<UIGraphNode, String>();
        map.put(v1, "CSE");
        map.put(v2, "IT");

        for(UIGraphNode n : map.keySet())
        {
            System.out.println(map.get(n).toString());
        }
    }

    @Override
    public int compareTo(UIGraphNode node2) {
        return xpath.compareTo(node2.getXpath());
    }

}