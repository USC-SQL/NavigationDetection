package edu.usc.WinnTree;

import ai.onnxruntime.OrtException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.usc.Utilities.LoadConfig;
import edu.usc.WinnTree.Construction.ConstructWinnTree;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WinnTree {

    private FunctionalArea root;

    public Set<FunctionalArea> vertex_set;

    public Set<FunctionalArea> explored_vertices;

    public WinnTree() {
        this.root = null;
        this.vertex_set = new HashSet<>();
        this.explored_vertices = new HashSet<>();
    }

    public FunctionalArea getRoot() {
        return root;
    }

    public void setRoot(FunctionalArea root) {
        this.root = root;
    }

    public Set<FunctionalArea> getVertexSet() {
        return vertex_set;
    }

    public void setVertex_set(Set vertex_set) {
        this.vertex_set = vertex_set;
    }

    public Set<FunctionalArea> getExploredSet() {
        return explored_vertices;
    }

    public void resetExploredSet() {
        this.explored_vertices = new HashSet<>();
    }

    public void AddExplored(FunctionalArea FA) {
        explored_vertices.add(FA);
        UpdateExplored();
    }

    public void UpdateExplored(){
        Set<FunctionalArea> update_set = new HashSet<>();
        for (FunctionalArea FA : vertex_set) {
            Set<FunctionalArea> children = new HashSet<>(FA.getChildren());
            if (explored_vertices.containsAll(children)) {
                update_set.add(FA);
            }
        }
        explored_vertices.addAll(update_set);
    }

    public void Build(LoadConfig configs, String subject) throws OrtException, InterruptedException, IOException {
        ConstructWinnTree new_tree = new ConstructWinnTree();
        FunctionalArea root = new_tree.Construct(configs, subject);
        setRoot(root);
    }

    public boolean IsHorizontallyAligned(FunctionalArea A, FunctionalArea B){
        Double A_ycoor = Double.valueOf(A.getMBR().get(1));
        Double B_ycoor = Double.valueOf(B.getMBR().get(1));
        if((B_ycoor * 0.95) <= A_ycoor && A_ycoor <= (B_ycoor * 1.05)){
            return true;
        }
        if((A_ycoor * 0.95) <= B_ycoor && B_ycoor <= (A_ycoor * 1.05)){
            return true;
        }
        return false;
    }

    public void Load(LoadConfig configs_obj, String subject) throws IOException {
        //Finding and loading JSON file for subject
        System.out.println("Beginning loading W-tree for subject: " + subject);
        String path = configs_obj.getProperties().getProperty("KFG_graph_location") + File.separator + subject + File.separator + subject + ".json";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File(path));

        //Building the Root node (i.e., root FA)
        ArrayNode node_mbr = (ArrayNode) jsonNode.get("MBR");
        List<Integer> root_mbr = new ArrayList<>();
        for(JsonNode dataNode: node_mbr){
            root_mbr.add(dataNode.asInt());
        }
        FunctionalArea root = new FunctionalArea(jsonNode.get("id").asText(), "", "", root_mbr);
        setRoot(root);

        //Transforming all JsonNodes into functional areas
        Set<FunctionalArea> all_vertices = new HashSet<>();
        all_vertices.add(root);
        Queue<JsonNode> queue = new LinkedList<JsonNode>();
        for(JsonNode root_children: jsonNode.get("children")){
            queue.add(root_children);
        }
        while (!queue.isEmpty()) {
            JsonNode tempNode = queue.poll();
            String xpath = tempNode.get("xpath").asText();
            String id = tempNode.get("id").asText();
            String parent_id = tempNode.get("parent_id").asText();
            ArrayNode tempNode_mbr = (ArrayNode) tempNode.get("MBR");
            List<Integer> mbr = new ArrayList<>();
            for(JsonNode coord: tempNode_mbr){
                mbr.add(coord.asInt());
            }
            FunctionalArea vertex = new FunctionalArea(id, xpath, parent_id, mbr);
            all_vertices.add(vertex);
            for(JsonNode node_children: tempNode.get("children")){
                queue.add(node_children);
            }
        }

        //Formally building the W-tree
        for(FunctionalArea parent: all_vertices){
            for(FunctionalArea child: all_vertices){
                if(child.getParentID().equals(parent.getID())){
                    parent.addChild(child);
                }
            }
        }
        setVertex_set(all_vertices);
        System.out.println("Loading of W-tree has completed.");
    }

    public FunctionalArea FindByXpath(String xpath){
        FunctionalArea temp = new FunctionalArea("", "", "", new ArrayList<>());
        for(FunctionalArea FA: this.getVertexSet()){
            if(FA.getXpath().equals(xpath)){
               return FA;
            }
        }
        System.out.println("Error - couldn't find FA with xpath: " + xpath);
        return temp;
    }

    public List FindDFSPath(FunctionalArea goal){
        Stack<List> stack = new Stack<>();
        List element = new ArrayList();
        List path = new ArrayList();
        path.add(root);
        element.add(root);
        element.add(path);
        stack.add(element);
        Set<FunctionalArea> visited = new HashSet<>();
        while(!stack.isEmpty()){
            List current_element = stack.pop();
            List current_path = (List) current_element.get(1);
            FunctionalArea current_FA = (FunctionalArea) current_element.get(0);
            if(!visited.contains(current_FA)) {
                if (current_FA.equals(goal)) {
                    return current_path;
                }
                visited.add(current_FA);
                for (FunctionalArea child : current_FA.getChildren()) {
                    List new_list = new ArrayList<>(current_path);
                    new_list.add(child);
                    List new_element = new ArrayList<>();
                    new_element.add(child);
                    new_element.add(new_list);
                    stack.add(new_element);
                }
            }
        }
        return path;
    }
}
