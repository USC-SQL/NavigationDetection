package edu.usc.WinnTree.Construction.AffinityGraph;

import edu.usc.WinnTree.FunctionalArea;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import static edu.usc.WinnTree.Construction.AffinityCalculation.AffinityScore.CalculateAffinityScore;

public class AffinityCompleteGraph {

    public Set<FunctionalArea> vertex_set;
    public Set<ACG_edge> edge_set;

    public AffinityCompleteGraph(){
        this.vertex_set = new HashSet<>();
        this.edge_set = new HashSet<>();
    }

    public void AddVertex(FunctionalArea vertex){
        vertex_set.add(vertex);

        //updates the Complete Graph with new edges connecting to new vertex
        Iterator<FunctionalArea> setIterator = vertex_set.iterator();
        while(setIterator.hasNext()){
            FunctionalArea check_vertex = setIterator.next();
            if(!vertex.equals(check_vertex)){
                AddEdge(vertex, check_vertex);
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
        double affinity_score = CalculateAffinityScore(vertexOne, vertexTwo);
        new_edge.setAffinity_Score(affinity_score);
        edge_set.add(new_edge);
    }

}
