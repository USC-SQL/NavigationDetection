package edu.usc.KFG.KFFG;

import edu.usc.KFG.UIGraph.UIGraphEdge;

import java.util.HashMap;
import java.util.Map;

public class KFFGEdge extends UIGraphEdge {

//	KNFGNode v1;
//	KNFGNode v2;
//	String phi;

    public KFFGEdge(KFFGNode v1, KFFGNode v2, String phi) {
        // TODO Auto-generated constructor stub
        this.v1 = v1;
        this.v2 = v2;
        this.var = phi;
    }

//	public KNFGNode getV1() {
//		return v1;
//	}
//
//	public KNFGNode getV2() {
//		return v2;
//	}

    public String getPhi() {
        return var;
    }

//	@Override
//    public boolean equals(Object o) {
//
//        if (o == this) return true;
//        if (!(o instanceof KNFGEdge)) {
//            return false;
//        }
//
//        KNFGEdge knfgedge = (KNFGEdge) o;
//
//        return knfgedge.getV1().equals(v1) &&
//        		knfgedge.getV2().equals(v2) &&
//        		knfgedge.getPhi().equals(phi);
//    }
//
//    //Idea from effective Java : Item 9
//    @Override
//    public int hashCode() {
//        int result = 17;
//        result = 31 * result + v1.hashCode();
//        result = 31 * result + v2.hashCode();
//        result = 31 * result + phi.hashCode();
//        return result;
//    }


    @Override
    public String toString() {
        return "[v1: " + v1 + ",  v2: " + v2 + ",  phi: " + var + "]";
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        KFFGNode v1 = new KFFGNode("kk");
        KFFGNode v2 = new KFFGNode("km");
        KFFGNode v3 = new KFFGNode("kr");

        System.out.println(v1.equals(v2));

        KFFGEdge e1 = new KFFGEdge(v1, v3, "tab");
        KFFGEdge e2 = new KFFGEdge(v1, v3, "tab");
        KFFGEdge e3 = new KFFGEdge(v2, v3, "tab");
        KFFGEdge e4 = new KFFGEdge(v2, v1, "tab");

        Map<KFFGEdge, String> map = new HashMap<KFFGEdge, String>();
        map.put(e1, "CSE");
        map.put(e2, "IT");
        map.put(e3, "CS");
        map.put(e4, "EE");

        for(KFFGEdge n : map.keySet())
        {
            System.out.println(map.get(n).toString());
        }

    }

//	@Override
//	public int compareTo(KNFGEdge edge2) {
//        return v1.getXpath().compareTo(edge2.getV1().getXpath());
//    }

}
