package edu.usc.KFG.UIGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.usc.KFG.KFFG.KFFGNode;
import edu.usc.KFG.UIGraph.misc.KWALIEdge;
import edu.usc.KFG.UIGraph.misc.KWALIElementWrapper;
import edu.usc.KFG.Pair;


public class UIGraphState implements Comparable<UIGraphState> {

	String uiGraphType;
	Set<KWALIElementWrapper> ctrlElems;
	Set<KWALIEdge> edgeSet;
	
	//Set<KWALIElementWrapper> ctrlElemsWithKeyboardEvents;
	
	Set<ArrayList<Pair<KWALIElementWrapper, String>>> crawlTraceToThisState;		// set of crawl-traces leading to current state
	List<String> visibleDomXpaths;
	
	UIGraph uigraph;
	String createdAtWhatPhase = "";

	
	Set<Integer> parentUniversalStateIDs;
	int parentUniversalStateID;
	int universalStateID;
	//int childUniversalStateID = -1;

	
	// keep track of non-valid elements in edgesAll
	Set<String> invisibleNodesXpathInEdgesAll;
	public Set<String> getInvisibleNodesXpathInEdgesAll() {
		return invisibleNodesXpathInEdgesAll;
	}
	public void setInvisibleNodesXpathInEdgesAll(Set<String> invisibleNodesXpathInEdgesAll) {
		this.invisibleNodesXpathInEdgesAll = invisibleNodesXpathInEdgesAll;
	}	
	
	
	

	/////// knf paper new addition 
	boolean isADialogState;				
	public boolean isADialogState() {
		return isADialogState;
	}
	public void setADialogState(boolean isADialogState) {
		this.isADialogState = isADialogState;
	}
	// for detecting whether a pair of states exhibits a dialog transition
	List<String> visibleDomInsideDialogXpaths;
	public List<String> getVisibleDomInsideDialogXpaths() {
		return visibleDomInsideDialogXpaths;
	}
	public void setVisibleDomInsideDialogXpaths(List<String> visibleDomInsideDialogXpaths) {
		this.visibleDomInsideDialogXpaths = visibleDomInsideDialogXpaths;
	}	
	
	// triggers
	public UIGraphNode triggerThatActivatedThisCurrentModalState;
	public UIGraphNode getTriggerThatActivatedThisCurrentModalState() {
		return triggerThatActivatedThisCurrentModalState;
	}
	public void setTriggerThatActivatedThisCurrentModalState(UIGraphNode triggerThatActivatedThisCurrentModalState) {
		this.triggerThatActivatedThisCurrentModalState = triggerThatActivatedThisCurrentModalState;
	}	
	
	// clusters
	public ArrayList<ArrayList<String>> ctrlElementClusters;// = new ArrayList<ArrayList<String>>();
	public ArrayList<ArrayList<String>> getCtrlElementClusters() {
		return ctrlElementClusters;
	}
	public void setCtrlElementClusters(ArrayList<ArrayList<String>> ctrlElementClusters) {
		this.ctrlElementClusters = ctrlElementClusters;
	}


	
	
	
//	Set<FunctionalityMerged> mergedFunctionalities = new HashSet<FunctionalityMerged>();
//	public Set<FunctionalityMerged> getMergedFunctionalities() {
//		return mergedFunctionalities;
//	}
//	public void setMergedFunctionalities(Set<FunctionalityMerged> mergedFunctionalities) {
//		this.mergedFunctionalities = mergedFunctionalities;
//	}	
//	public void addToMergedFunctionalities(FunctionalityMerged mergedFunctionality) {
//		this.mergedFunctionalities.add(mergedFunctionality);
//	}	

	// v_0 is inside the KNFG
	UIGraphNode v_entry;
	UIGraphNode v_exit;
	List<UIGraphEdge> TraversalOrder;
	//UIGraphNode v_transition;
	//Pair<UIGraphNode, String>
	
	// KFFG
	UIGraphNode v_entry_successor = new UIGraphNode("null");
	
	
	//List<Pair<Pair<Pair<UIGraphNode, String>, Integer>, UIGraphNode>> nextStateTransitionList;			// old for KNFG
	// new for KFFG, wrapped in another layer and added right-hand-side to pair
	List<Pair<Pair<Pair<UIGraphNode, String>, Integer>, Pair<UIGraphNode, UIGraphNode>>> nextStateTransitionList;
	
	
	
	Set<Pair<UIGraphNode, String>> changeOfContextTrapsSet;// why need list?  why can't be set?


	
	public UIGraphState(String uiGraphType) {
		this.ctrlElems = new HashSet<KWALIElementWrapper>();
		this.edgeSet = new HashSet<KWALIEdge>();
		
		//this.ctrlElemsWithKeyboardEvents = new HashSet<KWALIElementWrapper>();
		
		this.uiGraphType = uiGraphType;
		this.crawlTraceToThisState = new HashSet<ArrayList<Pair<KWALIElementWrapper, String>>>();
		
		//this.previousStateTransitionList = new ArrayList<Pair<Pair<Pair<UIGraphNode, String>, Integer>, UIGraphNode>>();
		this.nextStateTransitionList = new ArrayList<Pair<Pair<Pair<UIGraphNode, String>, Integer>, Pair<UIGraphNode, UIGraphNode>>>();
		this.changeOfContextTrapsSet = new HashSet<Pair<UIGraphNode, String>>();
		
		this.parentUniversalStateIDs = new HashSet<Integer>();

	}

//	public DOMState(Set<KWALIElementWrapper> ctrlElems) {
//		this.ctrlElems = ctrlElems;		
//		this.crawlTraceToThisState = new HashSet<ArrayList<Pair<KWALIElementWrapper, String>>>();
//	}
//	
//	public DOMState(Set<ArrayList<Pair<KWALIElementWrapper, String>>> crawlTraceToThisState, Set<KWALIElementWrapper> ctrlElems, Set<KWALIEdge> edgeSet) {
//		this.ctrlElems = ctrlElems;
//		this.edgeSet = edgeSet;
//		
//		this.crawlTraceToThisState = crawlTraceToThisState;
//	}

	public void GetOrder() {
		List<UIGraphEdge> order = new ArrayList<>();
		Set<UIGraphEdge> edges = this.getUigraph().getEdges();
		UIGraphNode end = this.getV_entry();
		//order.add(this.getV_entry());
		boolean check_end = true;
		while(check_end) {
			for(UIGraphEdge edge : edges) {
				if(edge.getV1().equals(end) && edge.getVar().equals("TAB")){
					end = edge.getV2();
					order.add(edge);
					check_end = true;
					break;
				}
				check_end = false;
			}
		}
		this.TraversalOrder = order;
	}

	public List<UIGraphEdge> getOrder() {
		return TraversalOrder;
	}



	public String getUiGraphType() {
		return uiGraphType;
	}
	public void setUiGraphType(String uiGraphType) {
		this.uiGraphType = uiGraphType;
	}


	public Set<KWALIElementWrapper> getCtrlElems() {		// the ones we actually interact with (less PCNFG extracted)
		return ctrlElems;
	}
	public void setCtrlElems(Set<KWALIElementWrapper> ctrlElems) {		// the ones we actually interact with (less PCNFG extracted)
		this.ctrlElems = ctrlElems;
	}


	public Set<KWALIEdge> getEdgeSet() {
		return edgeSet;
	}
	public void setEdgeSet(Set<KWALIEdge> edgeSet) {
		this.edgeSet = edgeSet;
	}

	
//	public Set<KWALIElementWrapper> getCtrlElemsWithKeyboardEvents() {
//		return ctrlElemsWithKeyboardEvents;
//	}
//	public void setCtrlElemsWithKeyboardEvents(Set<KWALIElementWrapper> ctrlElemsWithKeyboardEvents) {
//		this.ctrlElemsWithKeyboardEvents = ctrlElemsWithKeyboardEvents;
//	}	
	
	

	// visible elements used to represent and identify states
	public List<String> getVisibleDomXpaths() {
		return visibleDomXpaths;
	}
	public void setVisibleDomXpaths(List<String> visibleDomXpaths) {
		this.visibleDomXpaths = visibleDomXpaths;
	}

	


	public Set<ArrayList<Pair<KWALIElementWrapper, String>>> getCrawlTraceToThisState() {
		return crawlTraceToThisState;
	}

	public void setCrawlTraceToThisState(Set<ArrayList<Pair<KWALIElementWrapper, String>>> crawlTraceToThisState) {
		this.crawlTraceToThisState = crawlTraceToThisState;
	}

	public void addToCrawlTraceToThisState(ArrayList<Pair<KWALIElementWrapper, String>> crawlTraceToThisState) {
		this.crawlTraceToThisState.add(crawlTraceToThisState);
	}
	
	public boolean isExistInCrawlTraceToThisState(ArrayList<Pair<KWALIElementWrapper, String>> crawlTraceToThisState) {
		return this.crawlTraceToThisState.contains(crawlTraceToThisState);
	}
	
	
	

	public UIGraph getUigraph() {
		return uigraph;
	}

	public void setUigraph(UIGraph uigraph) {
		this.uigraph = uigraph;
	}


	public void setCreatedAtWhatPhase(String createdAtWhatPhase) {
		this.createdAtWhatPhase = createdAtWhatPhase;
	}
	public String getCreatedAtWhatPhase() {
		return createdAtWhatPhase;
	}

	
	
	public int getParentUniversalStateID() {
		return parentUniversalStateID;
	}
	public void setParentUniversalStateID(int parentUniversalStateID) {
		this.parentUniversalStateID = parentUniversalStateID;
	}	
	public Set<Integer> getParentUniversalStateIDs() {
		return parentUniversalStateIDs;
	}
	public Set<Integer> getAllParentUniversalStateIDs() {
		Set<Integer> allParentUniversalStateIDs = new HashSet<Integer>();
		allParentUniversalStateIDs.add(parentUniversalStateID);
		allParentUniversalStateIDs.addAll(parentUniversalStateIDs);
		return allParentUniversalStateIDs;
	}
	public void addToParentUniversalStateIDs(Integer parentUniversalStateID) {
		this.parentUniversalStateIDs.add(parentUniversalStateID);
	}
	
	
	
	public int getUniversalStateID() {
		return universalStateID;
	}
	public void setUniversalStateID(int universalStateID) {
		this.universalStateID = universalStateID;
	}
	
//	public int getChildUniversalStateID() {
//		return childUniversalStateID;
//	}
//	public void setChildUniversalStateID(int childUniversalStateID) {
//		this.childUniversalStateID = childUniversalStateID;
//	}

	public UIGraphNode getV_exit() {
		return v_exit;
	}
	public void setV_exit(UIGraphNode v_exit) {
		this.v_exit = v_exit;
	}

	
	public UIGraphNode getV_entry() {
		return v_entry;
	}
	public void setV_entry(UIGraphNode v_entry) {
		this.v_entry = v_entry;
	}
	public void setV_entry(KWALIElementWrapper v_entry) {
		this.v_entry = new KFFGNode(v_entry.getXpath());			// doesn't have to be same memory reference object because KEW and uigragh node "equals" has been overloaded
	}
	
	// *for KNF* the successor of v_entry (in case focus remains on trigger or is on the dialog container not inside dialog after the trigger)
	// (vice versa for after dialog dismissal)
	public UIGraphNode getV_entry_successor() {
		return v_entry_successor;
	}
	public void setV_entry_successor(UIGraphNode v_entry_successor) {
		this.v_entry_successor = v_entry_successor;
	}
	public void setV_entry_successor(KWALIElementWrapper v_entry_successor) {
		this.v_entry_successor = new KFFGNode(v_entry_successor.getXpath());			// doesn't have to be same memory reference object because KEW and uigragh node "equals" has been overloaded
	}
	

	


	public Set<Pair<UIGraphNode, String>> getChangeOfContextTrapsSet() {
		return changeOfContextTrapsSet;
	}
	public void setChangeOfContextTrapsSet(Set<Pair<UIGraphNode, String>> changeOfContextTraps) {
		this.changeOfContextTrapsSet = changeOfContextTraps;
	}
	public void addToChangeOfContextTrapsSet(Pair<UIGraphNode, String> changeOfContextTrap) {
		this.changeOfContextTrapsSet.add(changeOfContextTrap);
	}
	public void addToChangeOfContextTrapsSet(Set<Pair<UIGraphNode, String>> changeOfContextTraps) {
		this.changeOfContextTrapsSet.addAll(changeOfContextTraps);
	}
	public void addToChangeOfContextTrapsSet2(Set<Pair<KWALIElementWrapper, String>> changeOfContextTraps) {
		List<Pair<UIGraphNode, String>> changeOfContextTraps2 = new ArrayList<Pair<UIGraphNode, String>>();
		for(Pair<KWALIElementWrapper, String> pair:changeOfContextTraps) {
			changeOfContextTraps2.add(new Pair<UIGraphNode, String>(new UIGraphNode(pair.getLeft().getXpath()), pair.getRight()));
		}
		this.changeOfContextTrapsSet.addAll(changeOfContextTraps2);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
//	public List<Pair<Pair<Pair<UIGraphNode, String>, Integer>, UIGraphNode>> getPreviousStateTransitionList() {
//		return previousStateTransitionList;
//	}
//	public void addToPreviousStateTransition(UIGraphNode v_transition, String action, int destination, UIGraphNode v0) {
//		this.previousStateTransitionList.add(new Pair<Pair<Pair<UIGraphNode, String>, Integer>, UIGraphNode>(new Pair<Pair<UIGraphNode, String>, Integer>(new Pair<UIGraphNode, String>(v_transition, action), destination), v0));
//	}
//	public void addToPreviousStateTransition(KWALIElementWrapper v_transition, String action, int destination, KWALIElementWrapper v_0) {
//		this.previousStateTransitionList.add(new Pair<Pair<Pair<UIGraphNode, String>, Integer>, UIGraphNode>(new Pair<Pair<UIGraphNode, String>, Integer>(new Pair<UIGraphNode, String>(new KNFGNode(v_transition.getXpath()), action), destination), new KNFGNode(v_0.getXpath())));
//	}
//	public void setPreviousStateTransitionList(List<Pair<Pair<Pair<UIGraphNode, String>, Integer>, UIGraphNode>> previousStateTransitionList) {
//		this.previousStateTransitionList = previousStateTransitionList;
//	}
	
	public List<Pair<Pair<Pair<UIGraphNode, String>, Integer>, Pair<UIGraphNode, UIGraphNode>>> getNextStateTransitionList() {
		return nextStateTransitionList;
	}
	public void addToNextStateTransition(UIGraphNode v_transition, String action, int destination, UIGraphNode v_entry, UIGraphNode v_entry_successor) {
		this.nextStateTransitionList.add(new Pair<Pair<Pair<UIGraphNode, String>, Integer>, Pair<UIGraphNode, UIGraphNode>>(new Pair<Pair<UIGraphNode, String>, Integer>(new Pair<UIGraphNode, String>(v_transition, action), destination), new Pair<UIGraphNode, UIGraphNode>(v_entry, v_entry_successor)));
	}
	public void addToNextStateTransition(KWALIElementWrapper v_transition, String action, int destination, KWALIElementWrapper v_entry, KWALIElementWrapper v_entry_successor) {
		this.nextStateTransitionList.add(new Pair<Pair<Pair<UIGraphNode, String>, Integer>, Pair<UIGraphNode, UIGraphNode>>(new Pair<Pair<UIGraphNode, String>, Integer>(new Pair<UIGraphNode, String>(new KFFGNode(v_transition.getXpath()), action), destination), new Pair<UIGraphNode, UIGraphNode>(new KFFGNode(v_entry.getXpath()), new KFFGNode(v_entry_successor.getXpath()))));
	}
	public void setNextStateTransitionList(List<Pair<Pair<Pair<UIGraphNode, String>, Integer>, Pair<UIGraphNode, UIGraphNode>>> nextStateTransitionList) {
		this.nextStateTransitionList = nextStateTransitionList;
	}
	
//	public List<Pair<Pair<UIGraphNode, String>, Integer>> getNextStateTransitionList() {
//		return nextStateTransitionList;
//	}
//	public void addToNextStateTransition(UIGraphNode v_transition, String action, int destination) {
//		this.nextStateTransitionList.add(new Pair<Pair<UIGraphNode, String>, Integer>(new Pair<UIGraphNode, String>(v_transition, action), destination));
//	}
//	public void addToNextStateTransition(KWALIElementWrapper v_transition, String action, int destination) {
//		this.nextStateTransitionList.add(new Pair<Pair<UIGraphNode, String>, Integer>(new Pair<UIGraphNode, String>(new KNFGNode(v_transition.getXpath()), action), destination));
//	}
//	public void setNextStateTransitionList(List<Pair<Pair<UIGraphNode, String>, Integer>> nextStateTransitionList) {
//		this.nextStateTransitionList = nextStateTransitionList;
//	}

	
	
	
	
	
	/////////////////////////////////////////////////////
	//Idea from effective Java : Item 9
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + visibleDomXpaths.hashCode();
        return result;
    }
    
	@Override
	public int compareTo(UIGraphState state2) {
        return Integer.compare(getVisibleDomXpaths().size(), state2.getVisibleDomXpaths().size());
    }
	
	//////////// IMPORTANT TO decide whether a state has already been explored ////////////
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UIGraphState state2 = (UIGraphState) o;
        return getVisibleDomXpaths().equals(state2.getVisibleDomXpaths());
    }

	@Override
	public String toString() {
		return "DOMState [id=" + universalStateID + " uiGraphType=" + uiGraphType + " createdAtWhatPhase=" + createdAtWhatPhase + " visibleDomSize=" + getVisibleDomXpaths().size() + ", crawlTraceToThisState=" + crawlTraceToThisState + "]";
	}
	
	// print pretty ui state trace
	public void printUIStateTrace() {
		System.out.println(this.toString());
		System.out.println("v_entry:  " + this.getV_entry());
		for(ArrayList<Pair<KWALIElementWrapper, String>> crawlTrace : crawlTraceToThisState) {
			for(Pair<KWALIElementWrapper, String> pair : crawlTrace) {
				System.out.println(pair.getLeft().getXpath() + "--" + pair.getRight() + "-->");
			}
			System.out.println("");
		}
	}

}

