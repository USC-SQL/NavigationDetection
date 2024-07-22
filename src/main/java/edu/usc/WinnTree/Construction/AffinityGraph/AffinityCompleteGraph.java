package edu.usc.WinnTree.Construction.AffinityGraph;

import edu.usc.WinnTree.FunctionalArea;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AffinityCompleteGraph {

    public Set<FunctionalArea> vertex_set;
    public Set<ACG_edge> edge_set;

    public AffinityCompleteGraph(){
        this.vertex_set = new HashSet<>();
        this.edge_set = new HashSet<>();
    }

    public void AddVertex(FunctionalArea v1){
        vertex_set.add(v1);

        //updates the Complete Graph with new edges connecting to v1
        Iterator<FunctionalArea> setIterator = vertex_set.iterator();
        while(setIterator.hasNext()){
            FunctionalArea v2 = setIterator.next();
            if(!v1.equals(v2)){
                AddEdge(v1, v2);
            }
        }
    }

    public void RemoveVertex(FunctionalArea v){
        vertex_set.remove(v);

        //Updates complete graph to remove all edges containing v
        Iterator<ACG_edge> setIterator = edge_set.iterator();
        while(setIterator.hasNext()){
            ACG_edge edge = setIterator.next();
            Set<FunctionalArea> edge_vertices = edge.getVertices();
            if(edge_vertices.contains(v)){
                edge_set.remove(edge);
            }
        }
    }

    public void AddEdge(FunctionalArea v1, FunctionalArea v2) {
        ACG_edge new_edge = new ACG_edge(v1, v2);
        edge_set.add(new_edge);
    }

}
