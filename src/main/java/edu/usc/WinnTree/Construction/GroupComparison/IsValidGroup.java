package edu.usc.WinnTree.Construction.GroupComparison;

import edu.usc.WinnTree.Construction.AffinityGraph.ACG_edge;
import edu.usc.WinnTree.Construction.AffinityGraph.AffinityCompleteGraph;
import edu.usc.WinnTree.FunctionalArea;

import java.util.*;

import static edu.usc.WinnTree.Construction.GroupComparison.CheckMBR.ChecktheMBR;

public class IsValidGroup {

    public static Set ValidGroup(Set workset, AffinityCompleteGraph Affinity_graph){
        Set valid_group = new HashSet();
        List<ACG_edge> all_edges = Affinity_graph.GetSortedEdgeSet();
        Collections.reverse(all_edges);
        double value = 0;
        for(ACG_edge edge: all_edges){
            value = edge.getAffinity_Score();
            valid_group = edge.getVertices();
            if(ChecktheMBR(valid_group, workset)){
                break;
            }
        }
        List<ACG_edge> considered_edges = AddToGroup(all_edges, valid_group, value);
        //Looping through edges and trying to see if we can add more FA's to the current group
        Collections.sort(considered_edges, Comparator.comparing(ACG_edge::getAffinity_Score));
        Collections.reverse(considered_edges);
        for(ACG_edge edge: considered_edges){
            Set<FunctionalArea> candidates = edge.getVertices();
            candidates.addAll(valid_group);
            if(ChecktheMBR(candidates, workset)){
                valid_group.addAll(candidates);
            }
        }
        return valid_group;
    }

    public static List AddToGroup(List<ACG_edge> all_edges, Set<FunctionalArea> valid_group, double value){
        List<ACG_edge> considered_edges = new ArrayList<>();
        List<FunctionalArea> vertices = new ArrayList<FunctionalArea>(valid_group);
        FunctionalArea vertexOne = vertices.get(0);
        FunctionalArea vertexTwo = vertices.get(1);
        //finding all edges that connect to the current group's vertices and are within specified range
        for(ACG_edge edge: all_edges){
            Set<FunctionalArea> edge_vertices = edge.getVertices();
            if(edge_vertices.contains(vertexOne) || edge_vertices.contains(vertexTwo)){
                if(value >= edge.getAffinity_Score() && edge.getAffinity_Score() >= (value * 0.95)){
                    considered_edges.add(edge);
                }
            }
        }
        return considered_edges;
    }
}
