package edu.usc.KFG.KFFG;


import edu.usc.KFG.UIGraph.UIGraph;
import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphNode;
import edu.usc.KFG.UIGraph.misc.KWALIEdge;
import edu.usc.KFG.UIGraph.misc.KWALIElementWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KFFG extends UIGraph {

    KFFGNode v0;
    Set<KFFGNode> vn;

    List<KWALIElementWrapper> ctrlElemsInDialog;
    Set<UIGraphNode> nodesInDialog;

    //List<KWALIElementWrapper> ctrlElems_outsideDialog;
    List<KWALIEdge> edgeSet_inAndOutsideDialog;
    Set<UIGraphEdge> edges_inAndOutsideDialog;

    //Set<Pair<KWALIElementWrapper, String>> changeOfContextTraps;

    public KFFG() {
        this.ctrlElems = new ArrayList<KWALIElementWrapper>();
        this.edgeSet = new ArrayList<KWALIEdge>();

        //this.ctrlElems_outsideDialog = new ArrayList<KWALIElementWrapper>();
        this.edgeSet_inAndOutsideDialog = new ArrayList<KWALIEdge>();
        this.edges_inAndOutsideDialog = new HashSet<UIGraphEdge>();


        this.nodes = new HashSet<UIGraphNode>();
        this.edges = new HashSet<UIGraphEdge>();
        this.ctrlElemsInDialog = new ArrayList<KWALIElementWrapper>();
        this.nodesInDialog = new HashSet<UIGraphNode>();
        //this.changeOfContextTraps = new HashSet<Pair<KWALIElementWrapper, String>>();


        this.vn = new HashSet<KFFGNode>();	// to avoid null pointer when writing kffg to json
    }




    public KFFGNode getV0() {
        return v0;
    }
    public void setV0(KFFGNode v0) {						// used when reading in KNFG info from json
        this.v0 = v0;
    }
    public void setV0(KWALIElementWrapper kewV0) {
        this.v0 = new KFFGNode(kewV0.getXpath());			// doesn't have to be same memory reference object because KEW and uigragh node "equals" has been overloaded
    }

    public Set<KFFGNode> getVn() {
        return vn;
    }
    public void setVn(Set<KFFGNode> vn) {						// used when reading in KNFG info from json
        this.vn = vn;
    }
    public void setVnkew(Set<KWALIElementWrapper> kewVn) {
        Set<KFFGNode> vn = new HashSet<KFFGNode>();
        for(KWALIElementWrapper kew:kewVn) {
            vn.add(new KFFGNode(kew.getXpath()));
        }
        this.vn = vn;			// doesn't have to be same memory reference object because KEW and uigragh node "equals" has been overloaded
    }



    ////////// KNF stuff
    public List<KWALIElementWrapper> getCtrlElemsInDialog() {
        return ctrlElemsInDialog;
    }
    public Set<UIGraphNode> getNodesInDialog() {
        return nodesInDialog;
    }


    public void addNodeToCtrlElemsInDialog(KWALIElementWrapper kew) {
        ctrlElemsInDialog.add(kew);
    }

    // ouside dialog graph to establish reachability to [x]
//	public void addNodeToKNFG_outsideDialog(KWALIElementWrapper kew) {
//		ctrlElems_outsideDialog.add(kew);
//	}
    public void addEdgeToKNFG_inAndOutsideDialog(KWALIEdge edge) {
        edgeSet_inAndOutsideDialog.add(edge);
    }
    public Set<UIGraphEdge> getEdges_inAndOutsideDialog() {
        return edges_inAndOutsideDialog;
    }
    public void setEdges_inAndOutsideDialog(Set<UIGraphEdge> edges_inAndOutsideDialog) {
        this.edges_inAndOutsideDialog = edges_inAndOutsideDialog;
    }




    //public void addChangeOfContextTrapsToKFFG(Set<Pair<KWALIElementWrapper, String>> changeOfContextTraps) {		// KFEs
    //     this.changeOfContextTraps = changeOfContextTraps;
    //}

    public void addKFFGEdge(KFFGEdge edge) {
        this.edges.add(edge);
    }





    public void build() {
        // construct knfg node properties
        for(KWALIElementWrapper kew:ctrlElems) {
            KFFGNode node = new KFFGNode(kew.getXpath());
            node.setX(kew.getX_location());
            node.setY(kew.getY_location());
            node.setWidth(kew.getWidth());
            node.setHeight(kew.getHeight());

            // KNF
            node.setNotFocusedImgState(kew.getNotFocusedImgState());
            node.setFocusedImgState(kew.getFocusedImgState());
            node.setCorrdsOfInnerContentBox(kew.getCorrdsOfInnerContentBox());
            // KNF focus baseline
            node.setMinimumAreaFocusedImgStateCondition1(kew.getMinimumAreaFocusedImgStateCondition1());
            node.setMinimumAreaFocusedImgStateCondition2(kew.getMinimumAreaFocusedImgStateCondition2());
            node.setMinimumAreaFocusedImgStateCondition1b(kew.getMinimumAreaFocusedImgStateCondition1b());
            node.setMinimumAreaFocusedImgStateCondition2b(kew.getMinimumAreaFocusedImgStateCondition2b());

            nodes.add(node);
        }
        // construct knfg edge properties
        for(KWALIEdge ke:edgeSet) {
            KFFGNode v1 = new KFFGNode(ke.getV1().getXpath());
            v1.setX(ke.getV1().getX_location());
            v1.setY(ke.getV1().getY_location());
            v1.setWidth(ke.getV1().getWidth());
            v1.setHeight(ke.getV1().getHeight());
            // KNF
            v1.setNotFocusedImgState(ke.getV1().getNotFocusedImgState());
            v1.setFocusedImgState(ke.getV1().getFocusedImgState());

            KFFGNode v2 = new KFFGNode(ke.getV2().getXpath());
            v2.setX(ke.getV2().getX_location());
            v2.setY(ke.getV2().getY_location());
            v2.setWidth(ke.getV2().getWidth());
            v2.setHeight(ke.getV2().getHeight());
            // KNF
            v2.setNotFocusedImgState(ke.getV2().getNotFocusedImgState());
            v2.setFocusedImgState(ke.getV2().getFocusedImgState());

            edges.add(new KFFGEdge(v1, v2, ke.getKeystroke()));
        }

        // construct kffg dialog node properties
        for(KWALIElementWrapper kew:ctrlElemsInDialog) {
            KFFGNode node = new KFFGNode(kew.getXpath());
            node.setX(kew.getX_location());
            node.setY(kew.getY_location());
            node.setWidth(kew.getWidth());
            node.setHeight(kew.getHeight());

            // KNF
            node.setNotFocusedImgState(kew.getNotFocusedImgState());
            node.setFocusedImgState(kew.getFocusedImgState());
            node.setCorrdsOfInnerContentBox(kew.getCorrdsOfInnerContentBox());


            nodesInDialog.add(node);
        }

        // ouside dialog graph to establish reachability to [x]
        for(KWALIEdge ke:edgeSet_inAndOutsideDialog) {
            KFFGNode v1 = new KFFGNode(ke.getV1().getXpath());
            v1.setX(ke.getV1().getX_location());
            v1.setY(ke.getV1().getY_location());
            v1.setWidth(ke.getV1().getWidth());
            v1.setHeight(ke.getV1().getHeight());
            // KNF
            v1.setNotFocusedImgState(ke.getV1().getNotFocusedImgState());
            v1.setFocusedImgState(ke.getV1().getFocusedImgState());

            KFFGNode v2 = new KFFGNode(ke.getV2().getXpath());
            v2.setX(ke.getV2().getX_location());
            v2.setY(ke.getV2().getY_location());
            v2.setWidth(ke.getV2().getWidth());
            v2.setHeight(ke.getV2().getHeight());
            // KNF
            v2.setNotFocusedImgState(ke.getV2().getNotFocusedImgState());
            v2.setFocusedImgState(ke.getV2().getFocusedImgState());


            edges_inAndOutsideDialog.add(new KFFGEdge(v1, v2, ke.getKeystroke()));
        }

        /*
        // build change of context trap edges to v_exit
        for(Pair<KWALIElementWrapper, String> changeOfContextTrap : changeOfContextTraps) {// for future TO DELETE v_exit codes
            // handle special case of node "v_exit"
            KFFGNode v_exit = new KFFGNode(KWALIConfig.v_exit_nameStringInKNFG);					// need fix to global v_exit
            nodes.add(v_exit);// for future TO DELETE v_exit codes

            KFFGNode v1 = new KFFGNode(changeOfContextTrap.getLeft().getXpath());
            v1.setX(changeOfContextTrap.getLeft().getX_location());
            v1.setY(changeOfContextTrap.getLeft().getY_location());
            v1.setWidth(changeOfContextTrap.getLeft().getWidth());
            v1.setHeight(changeOfContextTrap.getLeft().getHeight());
            KFFGNode v2 = new KFFGNode(KWALIConfig.v_exit_nameStringInKNFG);		// for future TO DELETE v_exit codes
//			v2.setX(ke.getV2().getX_location());
//			v2.setY(ke.getV2().getY_location());
//			v2.setWidth(ke.getV2().getWidth());
//			v2.setHeight(ke.getV2().getHeight());
            edges.add(new KFFGEdge(v1, v2, changeOfContextTrap.getRight()));
        }// for future TO DELETE v_exit codes// for future TO DELETE v_exit codes// for future TO DELETE v_exit codes// for future TO DELETE v_exit codes


         */


    }

//	public Set<KNFGNode> getNodes() {
//		return nodes;
//	}
//	public Set<KNFGEdge> getEdges() {
//		return edges;
//	}
//
//	public void setNodes(Set<KNFGNode> nodes) {
//		this.nodes = nodes;
//	}
//	public void setEdges(Set<KNFGEdge> edges) {
//		this.edges = edges;
//	}
//
//	// quick look up
//	public KWALIElementWrapper getKEWByIndex(int index) {
//		for(KWALIElementWrapper kew:ctrlElems) {
//			if(kew.getThisElementIndex() == index) {
//				return kew;
//			}
//		}
//		return null;
//	}
//	public KWALIElementWrapper getKEWByXpath(String xpath) {
//		for(KWALIElementWrapper kew:ctrlElems) {
//			if(kew.getXpath().equals(xpath)) {
//				return kew;
//			}
//		}
//		return null;
//	}
//
//	public KWALIEdge getKWALIEdgeByXpaths(String v_s, String v_t) {
//		for(KWALIEdge ke:edgeSet) {
//			if(ke.getV1().getXpath().equals(v_s) && ke.getV2().getXpath().equals(v_t)) {
//				return ke;
//			}
//		}
//		return null;
//	}
//
//	public List<KWALIEdge> getKWALIEdgeByIncidentKEWIndex(int index) {
//		List<KWALIEdge> found = new ArrayList<KWALIEdge>();
//		for(KWALIEdge ke:edgeSet) {
//			if(ke.getV1().getThisElementIndex() == index || ke.getV2().getThisElementIndex() == index) {
//				found.add(ke);
//			}
//		}
//		return found;
//	}
/*
    @Override
    public String toString() {
        String output = "KNFG: \n";
        for(UIGraphEdge edge : edges) {
            KFFGNode v1 = (KFFGNode) edge.getV1();
            KFFGNode v2 = (KFFGNode) edge.getV2();
            output += "[" + v1.getXpath() + "] --" + ((KNFGEdge)edge).getPhi() + "--> [" + v2.getXpath() + "];\n";
        }
        return output;
    }

 */

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
