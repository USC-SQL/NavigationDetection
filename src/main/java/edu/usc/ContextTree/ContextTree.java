package edu.usc.ContextTree;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.usc.Utilities.LoadConfig;
import edu.usc.ContextTree.Construction.ConstructContextTree;
import org.json.JSONArray;
import org.json.simple.JSONObject;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ContextTree {

    private FunctionalArea root;

    public Set<FunctionalArea> vertex_set;

    //Keeps track of FA's that have been fully explored in the W-tree
    public Set<FunctionalArea> fully_explored_vertices;

    //Keeps track of FA's that have been visited at least once
    public Set<FunctionalArea> visited_vertices;

    public ContextTree() {
        this.root = null;
        this.vertex_set = new HashSet<>();
        this.fully_explored_vertices = new HashSet<>();
        this.visited_vertices = new HashSet<>();
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

    public Set<FunctionalArea> getFullyExploredSet() {
        return fully_explored_vertices;
    }

    public void resetFullyExploredSet() {
        this.fully_explored_vertices = new HashSet<>();
    }

    public Set<FunctionalArea> getVisitedSet() {
        return visited_vertices;
    }

    public void resetVisitedSet() {
        this.visited_vertices = new HashSet<>();
    }

    public void AddVisited(FunctionalArea FA) {
        visited_vertices.add(FA);
    }

    public void AddExplored(FunctionalArea FA) {
        fully_explored_vertices.add(FA);
        UpdateExplored();
    }

    public void UpdateExplored(){
        Set<FunctionalArea> update_set = new HashSet<>();
        int size = fully_explored_vertices.size();
        for (FunctionalArea FA : vertex_set) {
            Set<FunctionalArea> children = new HashSet<>(FA.getChildren());
            if (fully_explored_vertices.containsAll(children) && !children.isEmpty()) {
                update_set.add(FA);
            }
        }
        fully_explored_vertices.addAll(update_set);
        if(fully_explored_vertices.size() != size){ //handles cascading changes of 'visited' W-tree nodes
            UpdateExplored();
        }
    }

    public void Build(LoadConfig configs, String subject) throws InterruptedException, IOException {
        ConstructContextTree new_tree = new ConstructContextTree();
        FunctionalArea root = new_tree.Construct(configs, subject);
        System.out.println("Root's ID: " + root.getID());
        setRoot(root);
        Set<FunctionalArea> vertices = new HashSet<>();
        //Queue<FunctionalArea> queue = new LinkedList<FunctionalArea>();
        Stack<FunctionalArea> stack = new Stack<>();
        stack.push(root);
        while(!stack.isEmpty()){
            FunctionalArea vertex = stack.pop();
            vertices.add(vertex);
            for(FunctionalArea child: vertex.getChildren()){
                stack.push(child);
            }
        }
        setVertex_set(vertices);
        System.out.println("Finished C-tree Construction.");
    }

    public boolean IsHorizontallyAligned(FunctionalArea A, FunctionalArea B){
        Double A_ycoor = Double.valueOf(A.getMBR().get(1));
        Double B_ycoor = Double.valueOf(B.getMBR().get(1));
        if((B_ycoor * 0.99) <= A_ycoor && A_ycoor <= (B_ycoor * 1.01)){
            return true;
        }
        if((A_ycoor * 0.99) <= B_ycoor && B_ycoor <= (A_ycoor * 1.01)){
            return true;
        }
        return false;
    }

    public boolean IsVerticallyAligned(FunctionalArea A, FunctionalArea B){
        Double A_xcoor = Double.valueOf(A.getMBR().get(0));
        Double B_xcoor = Double.valueOf(B.getMBR().get(0));
        if((B_xcoor * 0.99) <= A_xcoor && A_xcoor <= (B_xcoor * 1.01)){
            return true;
        }
        if((A_xcoor * 0.99) <= B_xcoor && B_xcoor <= (A_xcoor * 1.01)){
            return true;
        }
        return false;
    }

    public void Load(LoadConfig configs_obj, String subject) throws IOException {
        //Finding and loading JSON file for subject
        System.out.println("Beginning loading C-tree for subject: " + subject);
        String path = configs_obj.GetCTreePath(subject);
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
        System.out.println("Loading of C-tree has completed.");
    }

    public void Save(LoadConfig configs_obj, String subject) throws IOException {
        System.out.println("Beginning saving C-tree via JSON for: " + subject);
        String path = configs_obj.GetCTreePath(subject);

        JSONObject root = new JSONObject();
        Set<JSONObject> all_nodes = new HashSet<>();
        for(FunctionalArea vertex: vertex_set){
            JSONObject node = new JSONObject();
            JSONArray children = new JSONArray();
            node.put("id", vertex.getID());
            node.put("MBR", vertex.getMBR());
            node.put("parent_id", vertex.getParentID());
            node.put("xpath", vertex.getXpath());
            node.put("children", children);
            all_nodes.add(node);
            if(vertex.getID().equals(this.root.getID())){
                root = node;
            }
        }

        List<JSONObject> node_list = new ArrayList<>(all_nodes);
        node_list.sort(Comparator.comparing(o -> Integer.parseInt((String) o.get("id"))));

        for(JSONObject node: node_list){
            String ID = (String) node.get("id");
            JSONArray children = (JSONArray) node.get("children");
            for(JSONObject other_nodes: node_list){
                String parent_ID = (String) other_nodes.get("parent_id");
                if(parent_ID.equals(ID)){
                    children.put(other_nodes);
                }
            }
        }

        FileWriter file = new FileWriter(path);
        file.write(root.toJSONString());
        file.flush();
        file.close();
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
