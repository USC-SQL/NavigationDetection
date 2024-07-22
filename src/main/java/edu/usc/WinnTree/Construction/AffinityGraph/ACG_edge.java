package edu.usc.WinnTree.Construction.AffinityGraph;

import edu.usc.WinnTree.FunctionalArea;

import java.util.HashSet;
import java.util.Set;

public class ACG_edge {
    public Set<FunctionalArea> vertex_pair;

    public double affinity_score; //ie weight of edge

    public ACG_edge(FunctionalArea vertex1, FunctionalArea vertex2) {
        this.vertex_pair = new HashSet<>();
        vertex_pair.add(vertex1);
        vertex_pair.add(vertex2);
    }

    public Set<FunctionalArea> getVertices() {
        return vertex_pair;
    }
    public void setAffinity_Score(double affinity_score) {
        this.affinity_score = affinity_score;
    }

    public double getAffinity_Score() {
        return affinity_score;
    }
}