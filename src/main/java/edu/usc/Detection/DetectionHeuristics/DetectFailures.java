package edu.usc.Detection.DetectionHeuristics;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.Utilities.LoadConfig;
import edu.usc.ContextTree.FunctionalArea;
import edu.usc.ContextTree.ContextTree;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.usc.Detection.DetectionHeuristics.MBROverlap.ConvexSkipFailure;
import static edu.usc.Detection.DetectionHeuristics.HorizontallyAligned.CheckHorizontallyAligned;
import static edu.usc.Detection.DetectionHeuristics.ReEntering.CheckReEnter;
import static edu.usc.Detection.DetectionHeuristics.VerticalAscent.CheckVerticalAscent;
import static edu.usc.Detection.DetectionHeuristics.CheckEntryExit.CheckEntryEdge;
import static edu.usc.Detection.DetectionHeuristics.EdgeIntoAligned.CheckEdgeIntoAligned;

public class DetectFailures {

    public static Set<UIGraphEdge> FindFailures(ContextTree Ctree, UIGraphState KFG, LoadConfig configs_obj, String subject) throws IOException {
        Set<UIGraphEdge> Skip_edges = new HashSet<>();
        Set<UIGraphEdge> ReEnter_edges = new HashSet<>();
        Set<UIGraphEdge> Backwards_edges = new HashSet<>();
        Set<UIGraphEdge> LeftChoice_edges = new HashSet<>();
        Set<UIGraphEdge> Top_edges = new HashSet<>();
        Set<UIGraphEdge> Bottom_edges = new HashSet<>();
        Set<UIGraphEdge> Vertical_edges = new HashSet<>();


        Set<UIGraphEdge> problematic_edges = new HashSet<>();
        List<UIGraphEdge> nav_order = KFG.getOrder(); //we trace the KFG to find failure edges
        FunctionalArea start = Ctree.FindByXpath(KFG.getV_entry().getXpath());
        FunctionalArea end = Ctree.FindByXpath(KFG.getV_exit().getXpath());
        Ctree.AddExplored(start);
        for(Object FA: Ctree.FindDFSPath(start)){
            FunctionalArea Visited_FA = (FunctionalArea) FA;
            Ctree.AddVisited(Visited_FA);
        }
        for(UIGraphEdge edge: nav_order){
            String source_xpath = edge.getV1().getXpath();
            String target_xpath = edge.getV2().getXpath();
            FunctionalArea source_FA = Ctree.FindByXpath(source_xpath);
            FunctionalArea target_FA = Ctree.FindByXpath(target_xpath);
            //Find ancestry of source and target nodes
            List<FunctionalArea> source_path = Ctree.FindDFSPath(source_FA);
            Collections.reverse(source_path);
            List<FunctionalArea> target_path = Ctree.FindDFSPath(target_FA);
            Collections.reverse(target_path);
            //if(MBRFailure(Wtree, source_xpath, target_xpath, source_FA, target_FA)){
            //    problematic_edges.add(edge);
            //    Skip_edges.add(edge);
            //    System.out.println("MBR Skip");
            //    System.out.println(source_FA.getID() + " -> " + target_FA.getID());
            //}
            if(ConvexSkipFailure(Ctree, source_xpath, target_xpath, source_FA, target_FA)){
                problematic_edges.add(edge);
                Skip_edges.add(edge);
                System.out.println("Skip");
                System.out.println(source_FA.getID() + " -> " + target_FA.getID());
            }
            if(CheckReEnter(Ctree, source_path, target_path)){
                problematic_edges.add(edge);
                ReEnter_edges.add(edge);
                System.out.println("ReEnter");
                System.out.println(source_FA.getID() + " -> " + target_FA.getID());
            }
            //if(CheckAllVisited(Wtree, source_path, target_path)){
            //    problematic_edges.add(edge);
            //}
            if(CheckHorizontallyAligned(Ctree, source_path, target_path)){
                problematic_edges.add(edge);
                System.out.println("Backwards");
                System.out.println(source_FA.getID() + " -> " + target_FA.getID());
                Backwards_edges.add(edge);
            }
            if(CheckEdgeIntoAligned(Ctree, source_path, target_path)){
                problematic_edges.add(edge);
                System.out.println("LeftChoice");
                System.out.println(source_FA.getID() + " -> " + target_FA.getID());
                LeftChoice_edges.add(edge);
            }
            if(CheckVerticalAscent(Ctree, source_path, target_path)){
                problematic_edges.add(edge);
                System.out.println("Vertical");
                System.out.println(source_FA.getID() + " -> " + target_FA.getID());
                Vertical_edges.add(edge);
            }
            Ctree.AddExplored(Ctree.FindByXpath(edge.getV2().getXpath())); //adds the target node to W-tree explored set
            Ctree.AddVisited(Ctree.FindByXpath(edge.getV2().getXpath())); //adds the target node to W-tree visited set
        }
        if(CheckEntryEdge(Ctree, start)){
            UIGraphEdge edge = new UIGraphEdge(KFG.getV_entry(), KFG.getV_entry(), "entry");
            problematic_edges.add(edge);
            Top_edges.add(edge);
        }

        //Uncomment below to save all the individual detections of the heuristics
        /*
        SaveDetectedFailures(Skip_edges, configs_obj, subject, "Skip.txt");
        SaveDetectedFailures(ReEnter_edges, configs_obj, subject, "ReEnter.txt");
        SaveDetectedFailures(Backwards_edges, configs_obj, subject, "Backwards.txt");
        SaveDetectedFailures(LeftChoice_edges, configs_obj, subject, "LeftChoice.txt");
        SaveDetectedFailures(Vertical_edges, configs_obj, subject, "VerticalAscent.txt");
        SaveDetectedFailures(Top_edges, configs_obj, subject, "Top.txt");
         */
        System.out.println("Skip edges: " + Skip_edges.size());
        System.out.println("Re-enter edges: " + ReEnter_edges.size());
        System.out.println("Backwards edges: " + Backwards_edges.size());
        System.out.println("LeftChoice edges: " + LeftChoice_edges.size());
        System.out.println("VerticalAscent edges: " + Vertical_edges.size());
        System.out.println("Top edges: " + Top_edges.size());
        return problematic_edges;
    }
}
