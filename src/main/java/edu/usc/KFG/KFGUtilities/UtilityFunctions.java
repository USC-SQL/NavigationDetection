package edu.usc.KFG.KFGUtilities;

import edu.usc.KFG.KFGUtilities.ReadKFFGFromJSON;
import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphNode;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.Utilities.LoadConfig;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UtilityFunctions {

    public static UIGraphState LoadTheKFG(LoadConfig configs_obj, String subject){
        System.out.println("Beginning loading KFG for subject: " + subject);
        String KFG_Dir = configs_obj.CheckKFGType() + File.separator + subject + File.separator;
        ReadKFFGFromJSON readKFFGState = new ReadKFFGFromJSON(KFG_Dir + "KFFG" + ".json", "KFFG");
        List<UIGraphState> finStateTotalKFFG = readKFFGState.getUIGraphStateList();
        UIGraphState KFG = finStateTotalKFFG.get(0);
        RemoveSelfEdges(KFG);
        CorrectStartEnd(KFG);
        KFG.GetOrder();
        System.out.println("Loading of KFG has completed.");
        return KFG;
    }

    public static void RemoveSelfEdges(UIGraphState KFG){
        Set<UIGraphEdge> edges = KFG.getUigraph().getEdges();
        Set<UIGraphEdge> self_edges = new HashSet<UIGraphEdge>();
        for(UIGraphEdge edge : edges) {
            if(edge.getV1().getXpath().equals(edge.getV2().getXpath())){
                self_edges.add(edge);
            }
        }
        edges.removeAll(self_edges);
    }

    public static void CorrectStartEnd (UIGraphState KFG){
        Set<UIGraphEdge> edges = KFG.getUigraph().getEdges();
        //find correct start
        UIGraphNode start = KFG.getV_entry();
        boolean check_start = true;
        while(check_start) {
            for(UIGraphEdge edge : edges) {
                if(edge.getV2().equals(start) && edge.getVar().equals("TAB")){
                    start = edge.getV1();
                    check_start = true;
                    break;
                }
                check_start = false;
            }
        }
        KFG.setV_entry(start);

        //find correct end
        UIGraphNode end = KFG.getV_entry();
        boolean check_end = true;
        while(check_end) {
            for(UIGraphEdge edge : edges) {
                if(edge.getV1().equals(end) && edge.getVar().equals("TAB")){
                    end = edge.getV2();
                    check_end = true;
                    break;
                }
                check_end = false;
            }
        }
        KFG.setV_exit(end);
        System.out.println("KFG Entry: " + KFG.getV_entry().getXpath());
        System.out.println("KFG Exit: " + KFG.getV_exit().getXpath());
    }
}
