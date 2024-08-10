package edu.usc.KFG.UIGraph;


import edu.usc.KFG.UIGraph.misc.KWALIEdge;
import edu.usc.KFG.UIGraph.misc.KWALIElementWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UIGraph {

    protected List<KWALIElementWrapper> ctrlElems;
    protected List<KWALIEdge> edgeSet;

    protected Set<UIGraphNode> nodes;
    protected Set<UIGraphEdge> edges;



    public UIGraph() {
        this.ctrlElems = new ArrayList<KWALIElementWrapper>();
        this.edgeSet = new ArrayList<KWALIEdge>();
        this.nodes = new HashSet<UIGraphNode>();
        this.edges = new HashSet<UIGraphEdge>();
    }



    public void addNodeToKNFG(KWALIElementWrapper kew) {
        ctrlElems.add(kew);
    }
    public void addEdgeToKNFG(KWALIEdge edge) {
        edgeSet.add(edge);
    }


    public List<KWALIElementWrapper> getCtrlElems() {
        return ctrlElems;
    }
    public List<KWALIEdge> getEdgeSet() {
        return edgeSet;
    }


//	public void build() {
//		// construct knfg node properties
//		for(KWALIElementWrapper kew:ctrlElems) {
//			KNFGNode node = new KNFGNode(kew.getXpath());
//			node.setX(kew.getX_location());
//			node.setY(kew.getY_location());
//			node.setWidth(kew.getWidth());
//			node.setHeight(kew.getHeight());
//			nodes.add(node);
//		}
//		// construct knfg edge properties
//		for(KWALIEdge ke:edgeSet) {
//			KNFGNode v1 = new KNFGNode(ke.getV1().getXpath());
//			v1.setX(ke.getV1().getX_location());
//			v1.setY(ke.getV1().getY_location());
//			v1.setWidth(ke.getV1().getWidth());
//			v1.setHeight(ke.getV1().getHeight());
//			KNFGNode v2 = new KNFGNode(ke.getV2().getXpath());
//			v2.setX(ke.getV2().getX_location());
//			v2.setY(ke.getV2().getY_location());
//			v2.setWidth(ke.getV2().getWidth());
//			v2.setHeight(ke.getV2().getHeight());
//			if(!ke.getEventDispathPhase().equals("")) {
//				edges.add(new KNFGEdge(v1, v2, ke.getEventDispathPhase()));
//			} else if(!ke.getEventType().equals("")) {
//				edges.add(new KNFGEdge(v1, v2, ke.getEventType()));
//			}
//
//		}
//	}

    public Set<UIGraphNode> getNodes() {
        return nodes;
    }
    public Set<UIGraphEdge> getEdges() {
        return edges;
    }

    public void setNodes(Set<UIGraphNode> nodes) {
        this.nodes = nodes;
    }
    public void setEdges(Set<UIGraphEdge> edges) {
        this.edges = edges;
    }

    // quick look up
    public KWALIElementWrapper getKEWByIndex(int index) {
        for(KWALIElementWrapper kew:ctrlElems) {
            if(kew.getThisElementIndex() == index) {
                return kew;
            }
        }
        return null;
    }
    public KWALIElementWrapper getKEWByXpath(String xpath) {
        for(KWALIElementWrapper kew:ctrlElems) {
            if(kew.getXpath().equals(xpath)) {
                return kew;
            }
        }
        return null;
    }

    public KWALIEdge getKWALIEdgeByXpaths(String v_s, String v_t) {
        for(KWALIEdge ke:edgeSet) {
            if(ke.getV1().getXpath().equals(v_s) && ke.getV2().getXpath().equals(v_t)) {
                return ke;
            }
        }
        return null;
    }

    public List<KWALIEdge> getKWALIEdgeByIncidentKEWIndex(int index) {
        List<KWALIEdge> found = new ArrayList<KWALIEdge>();
        for(KWALIEdge ke:edgeSet) {
            if(ke.getV1().getThisElementIndex() == index || ke.getV2().getThisElementIndex() == index) {
                found.add(ke);
            }
        }
        return found;
    }

}