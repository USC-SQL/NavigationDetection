package edu.usc.KFG;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;
import edu.usc.KFG.KFFG.KFFG;
import edu.usc.KFG.KFFG.KFFGEdge;
import edu.usc.KFG.KFFG.KFFGNode;
import edu.usc.KFG.UIGraph.UIGraph;
import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphNode;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.KFG.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;



public class ReadKFFGFromJSON {

    String jsonFilePath;
    String uiGraphType;

    // for retaining node's MBR info
    Map<String, KFFGNode> xpathKFFGNode;
    //Map<String, PCNFGNode> xpathPCNFGNode;

    String innerImageFolderPathName = "images";
    String innerDomObjFolderPathName = "doms";


    File writeToFileImageDir;
    File writeToFileDomDir;


    UIGraphState uiGraphState;				// make it global so other functions can retrieve already read-in properties (getUniversalStateID)


    public ReadKFFGFromJSON(String jsonFilePath, String uiGraphType) {
        this.jsonFilePath = jsonFilePath;
        this.uiGraphType = uiGraphType;
        this.xpathKFFGNode = new HashMap<String, KFFGNode>();
        //this.xpathPCNFGNode = new HashMap<String, PCNFGNode>();


        // get relative image dir for screenshot images
        File writeToFileBaseDir = new File(jsonFilePath).getParentFile();
        writeToFileImageDir = new File(Paths.get(writeToFileBaseDir.getAbsolutePath(), innerImageFolderPathName).toString());
        if(!writeToFileImageDir.exists()) {
            writeToFileImageDir.mkdirs();
        }
        writeToFileDomDir = new File(Paths.get(writeToFileBaseDir.getAbsolutePath(), innerDomObjFolderPathName).toString());
        if(!writeToFileDomDir.exists()) {
            writeToFileDomDir.mkdirs();
        }
    }



    public List<UIGraphState> getUIGraphStateList(){
        List<UIGraphState> uiGraphStateSet = new ArrayList<UIGraphState>();

        JSONObject uiGraphMainObj = null;

        // read-in json file and its main structure
        try {
            uiGraphMainObj = getJsonObjectFromJsonFile(jsonFilePath);
        } catch (Exception e1) {		System.out.println("Error while reading in JSON file.");			}

        // read-in ui graph states
        JSONArray uiGraphStateJsonArr = uiGraphMainObj.getJSONArray("UIGraphStates");

        // process each ui graph state json object
        for (int i = 0; i < uiGraphStateJsonArr.length(); i++) {
            try {

            } catch (Exception e) {		System.out.println("Error while parsing retrieved JSON data.");		}
            JSONObject uiGraphStateObj = (JSONObject) uiGraphStateJsonArr.get(i);


            // create ui graph object for inside this state
            UIGraph uiGraph = null;
            // underlying properties
            //Set<UIGraphEdge> uiGraphEdges = null;
            KFFGNode knfgV0 = null;//
            Set<KFFGNode> vn = null;


            UIGraphNode knfgVentry = null;
            UIGraphNode knfgVentrySuccessor = null;				// *KNF*

            //KNFGNode knfgVtransition = null;


            // initiate ui graph object based on identified graph type
            if(uiGraphType.equals("KFFG")) {
                uiGraph = new KFFG();
            } else if(uiGraphType.equals("PCNFG")) {
                //uiGraph = new PCNFG();
            }



            // KNFG nodes info
            Set<UIGraphNode> uiGraphNodes = getNodesFromJsonObject(uiGraphStateObj, uiGraphType);				// "nodes": []
            // KNFG edges info
            Set<UIGraphEdge> uiGraphEdges = getEdgesFromJsonObject(uiGraphStateObj, uiGraphType);				// "edges": []

            // assign retrieved UIGraph properties to ui graph object (properties inside UIGraph only)
            uiGraph.setNodes(uiGraphNodes);
            uiGraph.setEdges(uiGraphEdges);



            // KNFG edges info
            // ouside dialog graph to establish reachability to [x]
            Set<UIGraphEdge> uiGraphEdges_inAndOutsideDialog = getEdges_inAndOutsideDialogFromJsonObject(uiGraphStateObj, uiGraphType);
            ((KFFG)uiGraph).setEdges_inAndOutsideDialog(uiGraphEdges_inAndOutsideDialog);




            Map<String, UIGraphNode> vSpecialMap = getVspecialFromJsonObject(uiGraphStateObj);
            // KFFG v0 info
            if(uiGraphType.equals("KFFG")) {			knfgV0 = (KFFGNode) vSpecialMap.get("v_0");
                ((KFFG) uiGraph).setV0(knfgV0);			// handle v0 if KFFG
            }



            if(uiGraphType.equals("KFFG")) {
                vn = getVnFromJsonObject(uiGraphStateObj, uiGraphType);
                ((KFFG) uiGraph).setVn(vn);;			}



            List<Pair<Pair<Pair<UIGraphNode, String>, Integer>, Pair<UIGraphNode, UIGraphNode>>> stateTransitionsFromJsonObject = getStateTransitionsFromJsonObject(uiGraphStateObj);


            // create processed states from json
            uiGraphState = new UIGraphState(uiGraphType);

            // obtain and set state properties
            Map<String, String> uiGraphStatePropertiesMap = getStatePropertiesFromJsonObject(uiGraphStateObj);
            uiGraphState.setCreatedAtWhatPhase(uiGraphStatePropertiesMap.get("phase"));
            uiGraphState.setUniversalStateID(Integer.parseInt(uiGraphStatePropertiesMap.get("thisStateID")));														// set state id
            uiGraphState.setParentUniversalStateID(Integer.parseInt(uiGraphStatePropertiesMap.get("parentStateID")));

            // v_entry
            knfgVentry = vSpecialMap.get("v_entry");

            // *KNF* v_entry_successor
            knfgVentrySuccessor = vSpecialMap.get("v_entry_successor");



            uiGraphState.setV_entry(knfgVentry);													// v_entry of this UIGraph state
            uiGraphState.setV_entry_successor(knfgVentrySuccessor);									// *KNF* v_entry_successor
            //uiGraphState.setNextStateTransitionList(stateTransitionsFromJsonObject);				// state transition stuff

            // obtain and set visual ctrl Dom sequence
            List<String> visibleCtrlDomSequence = getVisibleCtrlDomSequenceFromJsonObject(uiGraphStateObj);
            uiGraphState.setVisibleDomXpaths(visibleCtrlDomSequence);


            /////////////// knf ///////////////
            // in dialog dom sequence
            List<String> visibleCtrlDomInsideDialogSequence = getVisibleCtrlDomInsideDialogSequenceFromJsonObject(uiGraphStateObj);
            uiGraphState.setVisibleDomInsideDialogXpaths(visibleCtrlDomInsideDialogSequence);
            // dialog state properties
            //Map<String, Object> dialogStateProperties = getDialogStatePropertiesFromJsonObject(uiGraphStateObj);
            // is a dialog state

            //boolean isDialogState = dialogStateProperties.get("isADialogState").equals("true") ? true : false;
            uiGraphState.setADialogState(false);



            UIGraphNode triggerNode = null;
            // trigger that leads to current state
            //if(dialogStateProperties.get("triggerNodeLeadingToState") != null) {
            //    triggerNode = new UIGraphNode((String) dialogStateProperties.get("triggerNodeLeadingToState"));		// remember to cast to String
            //}
            //uiGraphState.setTriggerThatActivatedThisCurrentModalState(triggerNode);



            // before/after DOM representation for outputting serialized DOM object file
            //DomNode preModalTriggerRoot = (DomNode) dialogStateProperties.get("preModalTriggerRoot");
            //DomNode postModalTriggerRoot = (DomNode) dialogStateProperties.get("postModalTriggerRoot");
            //uiGraphState.setPreModalTriggerRoot(preModalTriggerRoot);
            //uiGraphState.setPostModalTriggerRoot(postModalTriggerRoot);



            // cluster stuff
            ArrayList<ArrayList<String>> pageCtrlClusters = getCtrlClustersFromJsonObject(uiGraphStateObj);
            uiGraphState.setCtrlElementClusters(pageCtrlClusters);






            // change of context
            //Set<Pair<UIGraphNode, String>> changeOfContextTrapsList = getChangeOfContextTrapsFromJsonObject(uiGraphStateObj);
            //uiGraphState.setChangeOfContextTrapsSet(changeOfContextTrapsList);


            // set KNFG or PCNFG
            uiGraphState.setUigraph(uiGraph);



            // add to set of processed states
            uiGraphStateSet.add(uiGraphState);





            //Map<String, KNFGNode> uiGraphStatePropertiesMap = getVspecialFromJsonObject(uiGraphStateObj);








        }




        return uiGraphStateSet;
    }


    public JSONObject getJsonObjectFromJsonFile(String filePath) {
        File f = new File(filePath);
        InputStream is = null;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // start reading in json information
        JSONTokener tokener = new JSONTokener(is);
        JSONObject kNFGObj = new JSONObject(tokener);

        return kNFGObj;
    }

    public Set<UIGraphNode> getNodesFromJsonObject(JSONObject uiGraphObj, String uiGraphType) {
        Set<UIGraphNode> uiGraphNodes = new HashSet<UIGraphNode>();
        // constructs nodes info
        JSONArray nodesJsonArr = uiGraphObj.getJSONArray("nodes");						// "nodes": [
        for (int i = 0; i < nodesJsonArr.length(); i++) {								// 	        	{"xpath": "/html[1]/body[1]/div[2]/div[2]/form[1]/fieldset[1]/div[6]/div[1]/div[1]/div[1]/input[1]"},
            JSONObject nodeObj = (JSONObject) nodesJsonArr.get(i);						// 	        	{"xpath": "/html[1]/body[1]/div[2]/div[2]/form[1]/fieldset[1]/div[8]/a[1]"},
            UIGraphNode node = null;
            if(uiGraphType.equals("KFFG")) {
                node = new KFFGNode(nodeObj.get("xpath").toString());
            }
            String nodeMBR = nodeObj.get("MBR").toString();
            List<String> mBRCoords = Arrays.asList(nodeMBR.split(","));
            node.setX(Integer.parseInt(mBRCoords.get(0)));
            node.setY(Integer.parseInt(mBRCoords.get(1)));
            node.setWidth(Integer.parseInt(mBRCoords.get(2)));
            node.setHeight(Integer.parseInt(mBRCoords.get(3)));

            // image representation from JSON
            String nodeNotFocusedImgState = nodeObj.get("I").toString();
            String nodeFocusedImgState = nodeObj.get("I'").toString();

            if(nodeNotFocusedImgState.equals("null")) {
                ((KFFGNode)node).setNotFocusedImgState(null);
            }
            if(nodeFocusedImgState.equals("null")) {
                ((KFFGNode)node).setFocusedImgState(null);
            }

            // min area
            String minimumAreaFocusedImgIDCondition1 = nodeObj.get("minAreaBaseline1").toString();
            String minimumAreaFocusedImgIDCondition2 = nodeObj.get("minAreaBaseline2").toString();

            if(minimumAreaFocusedImgIDCondition1.equals("null")) {
                ((KFFGNode)node).setMinimumAreaFocusedImgStateCondition1(null);
            }
            if(minimumAreaFocusedImgIDCondition2.equals("null")) {
                ((KFFGNode)node).setMinimumAreaFocusedImgStateCondition2(null);
            }
            // min area b
            String minimumAreaFocusedImgIDCondition1b = nodeObj.get("minAreaBaseline1b").toString();
            String minimumAreaFocusedImgIDCondition2b = nodeObj.get("minAreaBaseline2b").toString();

            if(minimumAreaFocusedImgIDCondition1b.equals("null")) {
                ((KFFGNode)node).setMinimumAreaFocusedImgStateCondition1b(null);
            }
            if(minimumAreaFocusedImgIDCondition2b.equals("null")) {
                ((KFFGNode)node).setMinimumAreaFocusedImgStateCondition2b(null);
            }





            String nodeMBRContent = nodeObj.get("MBRContent").toString();
            if(nodeMBRContent.equals("null")) {
                ((KFFGNode)node).setCorrdsOfInnerContentBox(null);
            } else {
                List<String> mBRcorrdsOfInnerContentBox = Arrays.asList(nodeMBRContent.split(","));
                ArrayList<Integer> corrdsOfInnerContentBox = new ArrayList<Integer>();
                corrdsOfInnerContentBox.add(Integer.parseInt(mBRcorrdsOfInnerContentBox.get(0)));
                corrdsOfInnerContentBox.add(Integer.parseInt(mBRcorrdsOfInnerContentBox.get(1)));
                corrdsOfInnerContentBox.add(Integer.parseInt(mBRcorrdsOfInnerContentBox.get(2)));
                corrdsOfInnerContentBox.add(Integer.parseInt(mBRcorrdsOfInnerContentBox.get(3)));
                ((KFFGNode)node).setCorrdsOfInnerContentBox(corrdsOfInnerContentBox);
            }



            uiGraphNodes.add(node);
            if(uiGraphType.equals("KFFG")) {
                xpathKFFGNode.put(node.getXpath(), (KFFGNode) node);
            } else if(uiGraphType.equals("PCNFG")) {
                //xpathPCNFGNode.put(node.getXpath(), (PCNFGNode) node);
            }

        }
        return uiGraphNodes;
    }

    public Set<UIGraphEdge> getEdgesFromJsonObject(JSONObject uiGraphObj, String uiGraphType) {
        Set<UIGraphEdge> uiGraphEdges = new HashSet<UIGraphEdge>();							// "edges": [
        // constructs edges info														//             {
        JSONArray edgesJsonArr = uiGraphObj.getJSONArray("edges");						//                 "phi": "TAB",
        for (int i = 0; i < edgesJsonArr.length(); i++) {								//                 "v_t": "/html[1]/body[1]/div[2]/div[2]/form[1]/fieldset[1]/div[7]/div[4]/div[1]/input[1]",
            JSONObject nodeObj = (JSONObject) edgesJsonArr.get(i);						//                 "v_s": "/html[1]/body[1]/div[2]/div[2]/form[1]/fieldset[1]/div[7]/div[2]/div[1]/input[1]"
            if(uiGraphType.equals("KFFG")) {
                if(xpathKFFGNode.containsKey(nodeObj.get("v_s").toString()) && xpathKFFGNode.containsKey(nodeObj.get("v_t").toString())) {
                    uiGraphEdges.add(new KFFGEdge(xpathKFFGNode.get(nodeObj.get("v_s").toString()), xpathKFFGNode.get(nodeObj.get("v_t").toString()), nodeObj.get("phi").toString()));
                } else {
                    uiGraphEdges.add(new KFFGEdge(new KFFGNode(nodeObj.get("v_s").toString()), new KFFGNode(nodeObj.get("v_t").toString()), nodeObj.get("phi").toString()));
                }
            } else if(uiGraphType.equals("PCNFG")) {
//				if(xpathPCNFGNode.containsKey(nodeObj.get("v_s").toString()) && xpathPCNFGNode.containsKey(nodeObj.get("v_t").toString())) {
//					uiGraphEdges.add(new PCNFGEdge(xpathPCNFGNode.get(nodeObj.get("v_s").toString()), xpathPCNFGNode.get(nodeObj.get("v_t").toString()), nodeObj.get("var").toString()));
//				} else {
//					uiGraphEdges.add(new PCNFGEdge(new PCNFGNode(nodeObj.get("v_s").toString()), new PCNFGNode(nodeObj.get("v_t").toString()), nodeObj.get("var").toString()));
//				}
            }
        }
        return uiGraphEdges;
    }


    public Set<UIGraphEdge> getEdges_inAndOutsideDialogFromJsonObject(JSONObject uiGraphObj, String uiGraphType) {
        Set<UIGraphEdge> uiGraphEdges_inAndOutsideDialog = new HashSet<UIGraphEdge>();							// "edges": [
        // constructs edges info														//             {
        JSONArray edges_inAndOutsideDialogJsonArr = uiGraphObj.getJSONArray("edges_inAndOutsideDialog");						//                 "phi": "TAB",
        for (int i = 0; i < edges_inAndOutsideDialogJsonArr.length(); i++) {								//                 "v_t": "/html[1]/body[1]/div[2]/div[2]/form[1]/fieldset[1]/div[7]/div[4]/div[1]/input[1]",
            JSONObject nodeObj = (JSONObject) edges_inAndOutsideDialogJsonArr.get(i);						//                 "v_s": "/html[1]/body[1]/div[2]/div[2]/form[1]/fieldset[1]/div[7]/div[2]/div[1]/input[1]"
            if(uiGraphType.equals("KFFG")) {
                if(xpathKFFGNode.containsKey(nodeObj.get("v_s").toString()) && xpathKFFGNode.containsKey(nodeObj.get("v_t").toString())) {
                    uiGraphEdges_inAndOutsideDialog.add(new KFFGEdge(xpathKFFGNode.get(nodeObj.get("v_s").toString()), xpathKFFGNode.get(nodeObj.get("v_t").toString()), nodeObj.get("phi").toString()));
                } else {
                    uiGraphEdges_inAndOutsideDialog.add(new KFFGEdge(new KFFGNode(nodeObj.get("v_s").toString()), new KFFGNode(nodeObj.get("v_t").toString()), nodeObj.get("phi").toString()));
                }
            } else if(uiGraphType.equals("PCNFG")) {
//				if(xpathPCNFGNode.containsKey(nodeObj.get("v_s").toString()) && xpathPCNFGNode.containsKey(nodeObj.get("v_t").toString())) {
//					uiGraphEdges.add(new PCNFGEdge(xpathPCNFGNode.get(nodeObj.get("v_s").toString()), xpathPCNFGNode.get(nodeObj.get("v_t").toString()), nodeObj.get("var").toString()));
//				} else {
//					uiGraphEdges.add(new PCNFGEdge(new PCNFGNode(nodeObj.get("v_s").toString()), new PCNFGNode(nodeObj.get("v_t").toString()), nodeObj.get("var").toString()));
//				}
            }
        }
        return uiGraphEdges_inAndOutsideDialog;
    }

    public Map<String, UIGraphNode> getVspecialFromJsonObject(JSONObject uiGraphObj) {
        // constructs special nodes info
        Map<String, UIGraphNode> vSpecialMap = new HashMap<String, UIGraphNode>();
        JSONArray vSpecialArr = uiGraphObj.getJSONArray("v_special");							// "v0": [{"xpath": "/html[1]/body[1]/div[1]/header[1]/div[4]/p[1]/a[2]"}]
        for (int i = 0; i < vSpecialArr.length(); i++) {
            JSONObject nodeObj = (JSONObject) vSpecialArr.get(i);
            for(Iterator<String> iterator = nodeObj.keys(); iterator.hasNext();) {
                String attrbute = iterator.next();
                if(attrbute.equals("v_0"))	{																		// special case v0 for KNFG
                    vSpecialMap.put(attrbute, new KFFGNode(nodeObj.get(attrbute).toString()));
                } else if(attrbute.equals("v_entry_successor")) {
                    if(nodeObj.get(attrbute).toString().equals("null")) {
                        vSpecialMap.put(attrbute, null);
                    } else {
                        vSpecialMap.put(attrbute, new UIGraphNode(nodeObj.get(attrbute).toString()));
                    }
                } else {
                    vSpecialMap.put(attrbute, new UIGraphNode(nodeObj.get(attrbute).toString()));
                }
            }

        }




        // for v0 array, there's only one item (correspond to WriteKNFGToJSON)
        //JSONObject nodeObj = (JSONObject) v0JsonArr.get(0);


        //KNFGNode v0Node = new KNFGNode(nodeObj.get("xpath").toString());
        return vSpecialMap;
    }

    public Set<KFFGNode> getVnFromJsonObject(JSONObject uiGraphObj, String uiGraphType) {
        Set<KFFGNode> vn = new HashSet<KFFGNode>();
        // constructs nodes info
        JSONArray vnJsonArr = uiGraphObj.getJSONArray("v_n");						// "nodes": [

        for (int i = 0; i < vnJsonArr.length(); i++) {								// 	        	{"xpath": "/html[1]/body[1]/div[2]/div[2]/form[1]/fieldset[1]/div[6]/div[1]/div[1]/div[1]/input[1]"},
            JSONObject nodeObj = (JSONObject) vnJsonArr.get(i);						// 	        	{"xpath": "/html[1]/body[1]/div[2]/div[2]/form[1]/fieldset[1]/div[8]/a[1]"},
            if(uiGraphType.equals("KFFG")) {
                vn.add(new KFFGNode(nodeObj.get("xpath").toString())) ;
            }
        }
        return vn;
    }



    public Map<String, String> getStatePropertiesFromJsonObject(JSONObject uiGraphStateObj) {
        // constructs state properties
        Map<String, String> statePropertiesMap = new HashMap<String, String>();
        JSONArray statePropertiesArr = uiGraphStateObj.getJSONArray("state_properties");							// "v0": [{"xpath": "/html[1]/body[1]/div[1]/header[1]/div[4]/p[1]/a[2]"}]
        for (int i = 0; i < statePropertiesArr.length(); i++) {
            JSONObject nodeObj = (JSONObject) statePropertiesArr.get(i);
            for(Iterator<String> iterator = nodeObj.keys(); iterator.hasNext();) {
                String attrbute = iterator.next();
                statePropertiesMap.put(attrbute, nodeObj.get(attrbute).toString());
            }
        }
        return statePropertiesMap;
    }

    public List<Pair<Pair<Pair<UIGraphNode, String>, Integer>, Pair<UIGraphNode, UIGraphNode>>> getStateTransitionsFromJsonObject(JSONObject uiGraphObj) {
        List<Pair<Pair<Pair<UIGraphNode, String>, Integer>, Pair<UIGraphNode, UIGraphNode>>> nextStateTransitionList = new ArrayList<Pair<Pair<Pair<UIGraphNode, String>, Integer>, Pair<UIGraphNode, UIGraphNode>>>();							// "edges": [
        // constructs state transition info														//             {
        JSONArray stateTransitionArr = uiGraphObj.getJSONArray("stateTransition");						//                 "phi": "TAB",
        for (int i = 0; i < stateTransitionArr.length(); i++) {								//                 "v_t": "/html[1]/body[1]/div[2]/div[2]/form[1]/fieldset[1]/div[7]/div[4]/div[1]/input[1]",
            JSONObject nextStateTransitionObj = (JSONObject) stateTransitionArr.get(i);						//                 "v_s": "/html[1]/body[1]/div[2]/div[2]/form[1]/fieldset[1]/div[7]/div[2]/div[1]/input[1]"
            UIGraphNode v_transition = new UIGraphNode(nextStateTransitionObj.get("v_transition").toString());
            String action = nextStateTransitionObj.get("e_transition").toString();
            int destination = Integer.parseInt(nextStateTransitionObj.get("S_id").toString());
            UIGraphNode v_0_in_next = new UIGraphNode(nextStateTransitionObj.get("v_0_in_next").toString());
            UIGraphNode v_0_successor_in_next;
            if(!nextStateTransitionObj.get("v_0_successor_in_next").toString().equals("null")) {
                v_0_successor_in_next = new UIGraphNode(nextStateTransitionObj.get("v_0_successor_in_next").toString());
            } else {
                v_0_successor_in_next = new UIGraphNode("null");
            }


            nextStateTransitionList.add(new Pair<Pair<Pair<UIGraphNode, String>, Integer>, Pair<UIGraphNode, UIGraphNode>>(new Pair<Pair<UIGraphNode, String>, Integer>(new Pair<UIGraphNode, String>(v_transition, action), destination), new Pair<UIGraphNode, UIGraphNode>(v_0_in_next, v_0_successor_in_next)));
        }
        return nextStateTransitionList;
    }


    public List<String> getVisibleCtrlDomSequenceFromJsonObject(JSONObject uiGraphStateObj) {
        // constructs visible ctrl dom sequence properties
        TreeMap<Integer, String> visibleCtrlDomSequenceXpathMap = new TreeMap<Integer, String>();

        JSONArray visibleCtrlDomSequenceArr = uiGraphStateObj.getJSONArray("visibleCtrlDomSequence");							// "v0": [{"xpath": "/html[1]/body[1]/div[1]/header[1]/div[4]/p[1]/a[2]"}]
        for (int i = 0; i < visibleCtrlDomSequenceArr.length(); i++) {
            JSONObject nodeObj = (JSONObject) visibleCtrlDomSequenceArr.get(i);
            for(Iterator<String> iterator = nodeObj.keys(); iterator.hasNext();) {
                int sequenceIndex = Integer.parseInt(iterator.next());
                visibleCtrlDomSequenceXpathMap.put(sequenceIndex, nodeObj.get(sequenceIndex+"").toString());
            }
        }
        // convert into xpath list
        List<String> visibleCtrlDomSequence = new ArrayList<String>();
        for (Map.Entry<Integer, String> entry : visibleCtrlDomSequenceXpathMap.entrySet()) {
            visibleCtrlDomSequence.add(entry.getValue());
        }

        return visibleCtrlDomSequence;
    }


    //////////////////// knf //////////////////////////
    public List<String> getVisibleCtrlDomInsideDialogSequenceFromJsonObject(JSONObject uiGraphStateObj) {
        // constructs visible ctrl dom sequence properties
        TreeMap<Integer, String> visibleCtrlDomInsideDialogSequenceXpathMap = new TreeMap<Integer, String>();

        JSONArray visibleCtrlDomInsideDialogSequenceArr = uiGraphStateObj.getJSONArray("visibleCtrlDomInsideDialogSequence");							// "v0": [{"xpath": "/html[1]/body[1]/div[1]/header[1]/div[4]/p[1]/a[2]"}]
        for (int i = 0; i < visibleCtrlDomInsideDialogSequenceArr.length(); i++) {
            JSONObject nodeObj = (JSONObject) visibleCtrlDomInsideDialogSequenceArr.get(i);
            for(Iterator<String> iterator = nodeObj.keys(); iterator.hasNext();) {
                int sequenceIndex = Integer.parseInt(iterator.next());
                visibleCtrlDomInsideDialogSequenceXpathMap.put(sequenceIndex, nodeObj.get(sequenceIndex+"").toString());
            }
        }
        // convert into xpath list
        List<String> visibleCtrlDomInsideDialogSequence = new ArrayList<String>();
        for (Map.Entry<Integer, String> entry : visibleCtrlDomInsideDialogSequenceXpathMap.entrySet()) {
            visibleCtrlDomInsideDialogSequence.add(entry.getValue());
        }

        return visibleCtrlDomInsideDialogSequence;
    }

    // is dialog state


    // cluster stuff
    public ArrayList<ArrayList<String>> getCtrlClustersFromJsonObject(JSONObject uiGraphStateObj) {
        // get the page ctrl cluster info from json
        ArrayList<ArrayList<String>> pageCtrlClusters = new ArrayList<ArrayList<String>>();

        JSONArray clusterArr = uiGraphStateObj.getJSONArray("ctrlClusters");
        for (int i = 0; i < clusterArr.length(); i++) {
            ArrayList<String> cluster = new ArrayList<String>();
            JSONObject nodeObj = (JSONObject) clusterArr.get(i);
            for(Iterator<String> iterator = nodeObj.keys(); iterator.hasNext();) {
                cluster.add(nodeObj.get(iterator.next()).toString());
            }
            pageCtrlClusters.add(cluster);
        }

        return pageCtrlClusters;
    }



    // change of context
    public Set<Pair<UIGraphNode, String>> getChangeOfContextTrapsFromJsonObject(JSONObject uiGraphObj) {
        Set<Pair<UIGraphNode, String>> changeOfContextTrapsList = new HashSet<Pair<UIGraphNode, String>>();						// "edges": [
        // constructs change of context info														//             {
        JSONArray changeOfContextArr = uiGraphObj.getJSONArray("v_exit");						//                 "phi": "TAB",
        for (int i = 0; i < changeOfContextArr.length(); i++) {								//                 "v_t": "/html[1]/body[1]/div[2]/div[2]/form[1]/fieldset[1]/div[7]/div[4]/div[1]/input[1]",
            JSONObject changeOfContextTrapObj = (JSONObject) changeOfContextArr.get(i);
            UIGraphNode node = new UIGraphNode(changeOfContextTrapObj.get("v_s").toString());
            String phi = changeOfContextTrapObj.get("phi").toString();
            changeOfContextTrapsList.add(new Pair<UIGraphNode, String>(node, phi));
        }
        return changeOfContextTrapsList;
    }

    ////////////////////////// end knf ////////////////////////////////////

//	public String getUIGraphType(JSONObject uiGraphObj) {
//		// constructs graph type info
//		JSONArray graphJsonArr = uiGraphObj.getJSONArray("graph");						// "graph": [{"type": "KNFG"}]  or  "graph": [{"type": "PCNFG"}]
//		// for graph array, there's only one item (correspond to WriteKNFGToJSON)
//		JSONObject nodeObj = (JSONObject) graphJsonArr.get(0);
//		return nodeObj.get("type").toString();
//	}
//
//
//	////////////////////////////// GT retrieve
//
//
//
////		   "type1": [
////		      ["/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[5]/div[1]"],
////		      ["/html[1]/body[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[1]/form[1]/fieldset[1]/div[2]/div[1]/span[1]"],
////		      ["/html[1]/body[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[1]/form[1]/fieldset[1]/div[1]/div[1]/label[1]"],
////		      ["/html[1]/body[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[1]/form[1]/fieldset[1]/div[2]/div[1]/label[1]"],
////		      ["/html[1]/body[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[4]/div[1]"],
////		      ["/html[1]/body[1]/div[2]/div[2]/div[1]/div[1]/form[1]/fieldset[1]/div[1]/a[1]"]
////		   ]
////		}
//
//	public Set<HashSet<UIGraphNode>> getGT(String gtType) {
//		JSONObject gtObj = null;
//
//		try {
//			gtObj = getJsonObjectFromJsonFile(jsonFilePath);
//		} catch (Exception e1) {		System.out.println("Error while reading in JSON file.");			}
//
//
//		Set<HashSet<UIGraphNode>> components = new HashSet<HashSet<UIGraphNode>>();
//
//		// constructs gt info
//		JSONArray gtTypeJsonArr = gtObj.getJSONArray(gtType);								//	{
//		for(int i = 0; i < gtTypeJsonArr.length(); i++) {									//		   "type2": [],
//			JSONArray kafJsonArr = (JSONArray) gtTypeJsonArr.get(i);
//			HashSet<UIGraphNode> component = new HashSet<UIGraphNode>();
//			for(int j = 0; j < kafJsonArr.length(); j++) {
//				UIGraphNode kafNode = new UIGraphNode((String)kafJsonArr.get(j));
//				component.add(kafNode);
//			}
//			components.add(component);
//		}
//
//		return components;
//	}
//
////	public Set<HashSet<UIGraphNode>> getGTFromJsonObject(JSONObject gtObj, String gtType) {
////		Set<HashSet<UIGraphNode>> components = new HashSet<HashSet<UIGraphNode>>();
////
////		// constructs gt info
////		JSONArray gtTypeJsonArr = gtObj.getJSONArray(gtType);
////		for(int i = 0; i < gtTypeJsonArr.length(); i++) {
////			JSONArray kafJsonArr = (JSONArray) gtTypeJsonArr.get(i);
////			HashSet<UIGraphNode> component = new HashSet<UIGraphNode>();
////			for(int j = 0; j < kafJsonArr.length(); j++) {
////				UIGraphNode kafNode = new UIGraphNode((String)kafJsonArr.get(j));
////			}
////			components.add(component);
////		}
////
////		return components;
////	}
//
//
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//	}

}