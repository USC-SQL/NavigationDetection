package edu.usc.Detection.DetectionHeuristics;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphNode;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.KFG.UIGraph.misc.KWALIEdge;
import edu.usc.KFG.UIGraph.misc.KWALIElementWrapper;
import edu.usc.WinnTree.Construction.AffinityGraph.ACG_edge;
import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.usc.Detection.DetectionHeuristics.AlreadyVisited.CheckVisited;
import static edu.usc.Detection.DetectionHeuristics.MBROverlap.MBRFailure;
import static edu.usc.Detection.DetectionHeuristics.HorizontallyAligned.CheckHorizontallyAligned;

public class DetectFailures {

    public static Set<UIGraphEdge> FindFailures(WinnTree Wtree, UIGraphState KFG){
        Set<UIGraphEdge> problematic_edges = new HashSet<>();
        List<UIGraphEdge> nav_order = KFG.getOrder(); //we trace the KFG to find failure edges
        FunctionalArea start = Wtree.FindByXpath(KFG.getV_entry().getXpath());
        Wtree.AddExplored(start);
        for(UIGraphEdge edge: nav_order){
            String source_xpath = edge.getV1().getXpath();
            String target_xpath = edge.getV2().getXpath();
            FunctionalArea source_FA = Wtree.FindByXpath(source_xpath);
            FunctionalArea target_FA = Wtree.FindByXpath(target_xpath);
            //Find ancestry of source and target nodes
            List<FunctionalArea> source_path = Wtree.FindDFSPath(source_FA);
            Collections.reverse(source_path);
            List<FunctionalArea> target_path = Wtree.FindDFSPath(target_FA);
            Collections.reverse(target_path);
            if(MBRFailure(Wtree, source_xpath, target_xpath, source_FA, target_FA)){
                problematic_edges.add(edge);
            }
            if(CheckVisited(Wtree, source_path, target_path)){
                problematic_edges.add(edge);
            }
            if(CheckHorizontallyAligned(Wtree, source_path, target_path)){
                problematic_edges.add(edge);
            }
            Wtree.AddExplored(Wtree.FindByXpath(edge.getV2().getXpath())); //adds the target node to W-tree explored set
        }
        return problematic_edges;
    }
}
