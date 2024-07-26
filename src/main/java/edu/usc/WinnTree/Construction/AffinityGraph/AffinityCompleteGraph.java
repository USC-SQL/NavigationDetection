package edu.usc.WinnTree.Construction.AffinityGraph;

import ai.onnxruntime.OrtException;
import edu.usc.WinnTree.Construction.AffinityCalculation.SentenceComparer;
import edu.usc.WinnTree.FunctionalArea;

import java.util.*;

import static edu.usc.WinnTree.Construction.AffinityCalculation.AffinityScore.CalculateAffinityScore;

public class AffinityCompleteGraph {

    public Set<FunctionalArea> vertex_set;
    public Set<ACG_edge> edge_set;

    public SentenceComparer sentenceComparer_obj;
    public List<ACG_edge> edge_set_sorted;

    public AffinityCompleteGraph() throws OrtException {
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
        Iterator<ACG_edge> setIterator = edge_set.iterator();
        while(setIterator.hasNext()){
            ACG_edge edge = setIterator.next();
            Set<FunctionalArea> edge_vertices = edge.getVertices();
            if(edge_vertices.contains(vertex)){
                edge_set.remove(edge);
            }
        }
    }

    public void AddEdge(FunctionalArea vertexOne, FunctionalArea vertexTwo) {
        ACG_edge new_edge = new ACG_edge(vertexOne, vertexTwo);
        double affinity_score = 0;
        try {
            affinity_score = CalculateAffinityScore(vertexOne, vertexTwo, sentenceComparer_obj);
        } catch (OrtException e) {
            throw new RuntimeException(e);
        }
        new_edge.setAffinity_Score(affinity_score);
        edge_set.add(new_edge);
    }

    public void SortEdgeSet(){
        Collections.sort(edge_set_sorted, Comparator.comparing(ACG_edge::getAffinity_Score));
    }

    public List GetSortedEdgeSet(){
        return this.edge_set_sorted;
    }

}
