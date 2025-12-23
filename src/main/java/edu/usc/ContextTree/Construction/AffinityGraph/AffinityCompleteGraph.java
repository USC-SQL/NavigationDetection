package edu.usc.ContextTree.Construction.AffinityGraph;

import edu.usc.ContextTree.Construction.AffinityCalculation.SentenceComparer;
import edu.usc.ContextTree.FunctionalArea;
import java.util.*;

import static edu.usc.ContextTree.Construction.AffinityCalculation.AffinityScore.CalculateAffinityScore;

public class AffinityCompleteGraph {

    public Set<FunctionalArea> vertex_set;
    public Set<ACG_edge> edge_set;

    public SentenceComparer sentenceComparer_obj;
    public List<ACG_edge> edge_set_sorted;

    public AffinityCompleteGraph() {
        this.vertex_set = new HashSet<>();
        this.edge_set = new HashSet<>();
        this.sentenceComparer_obj = new SentenceComparer();
        this.edge_set_sorted = new ArrayList<>();
    }

    public void AddVertex(FunctionalArea new_vertex){
        vertex_set.add(new_vertex);

        //updates the Complete Graph with new edges connecting to new vertex
        Iterator<FunctionalArea> setIterator = vertex_set.iterator();
        while(setIterator.hasNext()){
            FunctionalArea check_vertex = setIterator.next();
            if(!new_vertex.equals(check_vertex)){
                AddEdge(new_vertex, check_vertex);
            }
        }
    }

    public void RemoveVertex(FunctionalArea vertex){
        vertex_set.remove(vertex);

        //Updates complete graph to remove all edges containing v
        Set<ACG_edge> remove_edges = new HashSet<>();
        Iterator<ACG_edge> setIterator = edge_set.iterator();
        while(setIterator.hasNext()){
            ACG_edge edge = setIterator.next();
            Set<FunctionalArea> edge_vertices = edge.getVertices();
            if(edge_vertices.contains(vertex)){
                remove_edges.add(edge);
            }
        }
        edge_set.removeAll(remove_edges);
    }

    public void AddEdge(FunctionalArea vertexOne, FunctionalArea vertexTwo) {
        ACG_edge new_edge = new ACG_edge(vertexOne, vertexTwo);
        double affinity_score = 0;
        List<Double> scores = CalculateAffinityScore(vertexOne, vertexTwo, sentenceComparer_obj);
        //affinity_score = CalculateAffinityScore(vertexOne, vertexTwo, sentenceComparer_obj);
        affinity_score = scores.get(0) + scores.get(1) + scores.get(2);
        new_edge.setAffinity_Score(affinity_score);
        edge_set.add(new_edge);
    }

    public void SortEdgeSet(){
        //only matters on the very first time - from then one it should always have an initialization
        this.edge_set_sorted = new ArrayList<>(this.edge_set);
        Collections.sort(edge_set_sorted, Comparator.comparing(ACG_edge::getAffinity_Score));
    }

    public List GetSortedEdgeSet(){
        return this.edge_set_sorted;
    }

    public void PrintEdgeSet(){
        for(ACG_edge edge: this.edge_set_sorted){
            System.out.println(edge.affinity_score);
        }
    }

}
