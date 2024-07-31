package edu.usc.KFG.KFFG;

import edu.usc.KFG.UIGraph.UIGraphNode;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KFFGNode extends UIGraphNode {//implements Comparable<KNFGNode> {

    BufferedImage notFocusedImgState;
    BufferedImage focusedImgState;
    ArrayList<Integer> corrdsOfInnerContentBox;

    BufferedImage minimumAreaFocusedImgStateCondition1;
    BufferedImage minimumAreaFocusedImgStateCondition2;
    BufferedImage minimumAreaFocusedImgStateCondition1b;
    BufferedImage minimumAreaFocusedImgStateCondition2b;








    public KFFGNode(String xpath) {
        // TODO Auto-generated constructor stub
        this.xpath = xpath;
    }


    public BufferedImage getNotFocusedImgState() {
        return notFocusedImgState;
    }
    public void setNotFocusedImgState(BufferedImage notFocusedImgState) {
        this.notFocusedImgState = notFocusedImgState;
    }


    public BufferedImage getFocusedImgState() {
        return focusedImgState;
    }
    public void setFocusedImgState(BufferedImage focusedImgState) {
        this.focusedImgState = focusedImgState;
    }



    public ArrayList<Integer> getCorrdsOfInnerContentBox() {
        return corrdsOfInnerContentBox;
    }
    public void setCorrdsOfInnerContentBox(ArrayList<Integer> corrdsOfInnerContentBox) {
        this.corrdsOfInnerContentBox = corrdsOfInnerContentBox;
    }






    public BufferedImage getMinimumAreaFocusedImgStateCondition1() {
        return minimumAreaFocusedImgStateCondition1;
    }
    public void setMinimumAreaFocusedImgStateCondition1(BufferedImage minimumAreaFocusedImgStateCondition1) {
        this.minimumAreaFocusedImgStateCondition1 = minimumAreaFocusedImgStateCondition1;
    }


    public BufferedImage getMinimumAreaFocusedImgStateCondition2() {
        return minimumAreaFocusedImgStateCondition2;
    }
    public void setMinimumAreaFocusedImgStateCondition2(BufferedImage minimumAreaFocusedImgStateCondition2) {
        this.minimumAreaFocusedImgStateCondition2 = minimumAreaFocusedImgStateCondition2;
    }


    public BufferedImage getMinimumAreaFocusedImgStateCondition1b() {
        return minimumAreaFocusedImgStateCondition1b;
    }
    public void setMinimumAreaFocusedImgStateCondition1b(BufferedImage minimumAreaFocusedImgStateCondition1b) {
        this.minimumAreaFocusedImgStateCondition1b = minimumAreaFocusedImgStateCondition1b;
    }


    public BufferedImage getMinimumAreaFocusedImgStateCondition2b() {
        return minimumAreaFocusedImgStateCondition2b;
    }
    public void setMinimumAreaFocusedImgStateCondition2b(BufferedImage minimumAreaFocusedImgStateCondition2b) {
        this.minimumAreaFocusedImgStateCondition2b = minimumAreaFocusedImgStateCondition2b;
    }











    @Override
    public String toString() {
        return "[" + xpath + "]";
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub

        KFFGNode v1 = new KFFGNode("kk");
        KFFGNode v2 = new KFFGNode("kk");

        System.out.println(v1.equals(v2));

        Map<KFFGNode, String> map = new HashMap<KFFGNode, String>();
        map.put(v1, "CSE");
        map.put(v2, "IT");

        for(KFFGNode n : map.keySet())
        {
            System.out.println(map.get(n).toString());
        }
    }

//	@Override
//	public int compareTo(KNFGNode node2) {
//        return xpath.compareTo(node2.getXpath());
//    }

}