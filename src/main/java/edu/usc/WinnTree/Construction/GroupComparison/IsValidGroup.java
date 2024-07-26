package edu.usc.WinnTree.Construction;

import edu.usc.WinnTree.Construction.AffinityGraph.ACG_edge;
import edu.usc.WinnTree.Construction.AffinityGraph.AffinityCompleteGraph;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IsValidGroup {

    public static Set ValidGroup(Set workset, AffinityCompleteGraph Affinity_graph){
        Set final_group = new HashSet();
        List<ACG_edge> all_edges = Affinity_graph.GetSortedEdgeSet();
        Collections.reverse(all_edges);

        double value = 0;
        for(ACG_edge edge: all_edges){
            value = edge.getAffinity_Score();

        }

        return final_group;
    }

}
