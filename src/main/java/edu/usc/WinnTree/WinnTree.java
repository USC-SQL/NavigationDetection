package edu.usc.WinnTree;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class WinnTree {

    private FunctionalArea root;

    public Set<FunctionalArea> vertex_set;

    public WinnTree() {
        this.root = null;
        this.vertex_set = null;
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

    public void setRoot(Set vertex_set) {
        this.vertex_set = vertex_set;
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

    public void Load(String subject) throws IOException {
        //Finding and loading JSON file for subject
        System.out.println("Beginning loading W-tree for subject: " + subject);
        String path = "C:/Users/rober/Documents/research/wtrees/groundtruth/" + subject + File.separator + subject + "_2.json";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File(path));

        //Building the Root node (i.e., root FA)
        ArrayNode node_mbr = (ArrayNode) jsonNode.get("MBR");
        List<Integer> root_mbr = new ArrayList<>();
        for(JsonNode dataNode: node_mbr){
            root_mbr.add(dataNode.asInt());
        }
        FunctionalArea root = new FunctionalArea(jsonNode.get("id").asText(), "", "", root_mbr);
        this.root = root;

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
        this.vertex_set = all_vertices;
        System.out.println("Loading of W-tree has completed.");
    }

}
