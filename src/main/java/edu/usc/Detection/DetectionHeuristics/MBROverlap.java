package edu.usc.Detection.DetectionHeuristics;

import edu.usc.ContextTree.FunctionalArea;
import edu.usc.ContextTree.ContextTree;

import java.awt.*;
import java.awt.geom.Area;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.usc.ContextTree.Construction.GroupComparison.CheckMBR.CalculauteMBR;
import static edu.usc.ContextTree.Construction.GroupComparison.CheckMBR.OverlappingMBR;

public class MBROverlap {

    public static boolean MBRFailure(ContextTree Wtree, String source_xpath, String target_xpath, FunctionalArea source_FA, FunctionalArea target_FA){
        Set Edge_group = new HashSet<>();
        Edge_group.add(source_FA);
        Edge_group.add(target_FA);
        List Edge_mbr = CalculauteMBR(Edge_group);
        for(FunctionalArea FA: Wtree.getVertexSet()){
            List FA_mbr = FA.getMBR();
            if(!FA.getXpath().equals("") && !FA.getXpath().equals(source_xpath) && !FA.getXpath().equals(target_xpath)) {
                if(OverlappingMBR(Edge_mbr, FA_mbr)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean ConvexSkipFailure(ContextTree Ctree, String source_xpath, String target_xpath, FunctionalArea source_FA, FunctionalArea target_FA) {
        Polygon convex_hull = new Polygon();
        List<Integer> source_MBR = source_FA.getMBR();
        List<Integer> target_MBR = target_FA.getMBR();
        convex_hull.addPoint(source_MBR.get(0), source_MBR.get(1));
        convex_hull.addPoint(source_MBR.get(0), source_MBR.get(3));
        convex_hull.addPoint(source_MBR.get(2), source_MBR.get(3));
        convex_hull.addPoint(source_MBR.get(2), source_MBR.get(1));
        convex_hull.addPoint(target_MBR.get(0), target_MBR.get(1));
        convex_hull.addPoint(target_MBR.get(0), target_MBR.get(3));
        convex_hull.addPoint(target_MBR.get(2), target_MBR.get(3));
        convex_hull.addPoint(target_MBR.get(2), target_MBR.get(1));
        //convex_hull.addPoint(target_MBR.get(2), target_MBR.get(3));

        for (FunctionalArea FA : Ctree.getVertexSet()) {
            List<Integer> FA_mbr = FA.getMBR();
            if (!FA.getXpath().equals("") && !FA.getXpath().equals(source_xpath) && !FA.getXpath().equals(target_xpath)) {
                if (!Ctree.getVisitedSet().contains(FA)) {
                    if(FA_mbr.get(1) - target_MBR.get(3) == -1){
                        int fix = FA_mbr.get(1) + 1;
                        FA_mbr.set(1, fix);
                    }
                    Polygon FA_polygon = new Polygon();
                    FA_polygon.addPoint(FA_mbr.get(0), FA_mbr.get(1));
                    FA_polygon.addPoint(FA_mbr.get(0), FA_mbr.get(3));
                    FA_polygon.addPoint(FA_mbr.get(2), FA_mbr.get(3));
                    FA_polygon.addPoint(FA_mbr.get(2), FA_mbr.get(1));
                    //FA_polygon.addPoint(FA_mbr.get(2), FA_mbr.get(3));
                    Area overlap = new Area(convex_hull);
                    overlap.intersect(new Area(FA_polygon));
                    if (!overlap.isEmpty() && CheckNonAlignmentPriorFailure(Ctree, target_FA, FA, source_FA) && !ContainedWithin(target_FA, FA)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static boolean CheckNonAlignmentPriorFailure(ContextTree Ctree, FunctionalArea A, FunctionalArea B, FunctionalArea source){
        //Checks if B is aligned and underneath A in a list
        if(Ctree.IsVerticallyAligned(A, B)){
            if(B.getMBR().get(1) >= A.getMBR().get(1)){
                return false;
            }
        }
        //Checks if B is aligned and to the right of A in a row
        if(Ctree.IsHorizontallyAligned(A, B)){
            if(B.getMBR().get(0) >= A.getMBR().get(0)){
                return false;
            }
        }
        return true;
    }

    public static boolean ContainedWithin(FunctionalArea target, FunctionalArea other){
        List<Integer> target_mbr = target.getMBR();
        List<Integer> other_mbr = other.getMBR();
        if(other_mbr.get(0) >= target_mbr.get(0) && target_mbr.get(2) >= other_mbr.get(2)){
            if(other_mbr.get(1) >= target_mbr.get(1) && target_mbr.get(3) >= other_mbr.get(3)){
                return true;
            }
        }
        return false;
    }


}
