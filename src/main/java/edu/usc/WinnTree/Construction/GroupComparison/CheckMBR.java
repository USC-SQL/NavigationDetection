package edu.usc.WinnTree.Construction.GroupComparison;

import edu.usc.WinnTree.FunctionalArea;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CheckMBR {

    //Returns true if the potential grouping of nodes (ie FAs) doesn't overlap any others in the workset
    //Returns false if an overlap of MBRs occurs
    public static boolean ChecktheMBR(Set<FunctionalArea> potential_group, Set<FunctionalArea> workset){
        Set check_set = workset;
        check_set.removeAll(potential_group);
        List potential_group_mbr = CalculauteMBR(potential_group);
        for(FunctionalArea FA: workset){
            List FA_mbr = FA.getMBR();
            if(OverlappingMBR(potential_group_mbr, FA_mbr)) {
                return false;
            }
        }
        return true;
    }

    public static List CalculauteMBR(Set<FunctionalArea> group){
        //We want top left to be as small as possible and bot right to be as big as possible
        int top_left_X = 9999;
        int top_left_Y= 9999;
        int bottom_right_X= 0;
        int bottom_right_Y= 0;
        for(FunctionalArea FA: group){
            List FA_mbr = FA.getMBR();
            if((int) FA_mbr.get(0) < top_left_X){
                top_left_X = (int) FA_mbr.get(0);
            }
            if((int) FA_mbr.get(1) < top_left_Y){
                top_left_Y = (int) FA_mbr.get(1);
            }
            if((int) FA_mbr.get(2) > bottom_right_X){
                bottom_right_X = (int) FA_mbr.get(2);
            }
            if((int) FA_mbr.get(3) > bottom_right_Y){
                bottom_right_Y = (int) FA_mbr.get(3);
            }
        }
        List Group_MBR = new ArrayList<>();
        Group_MBR.add(top_left_X);
        Group_MBR.add(top_left_Y);
        Group_MBR.add(bottom_right_X);
        Group_MBR.add(bottom_right_Y);
        return Group_MBR;
    }

    public static boolean OverlappingMBR(List MBR_one, List MBR_two) {
        //Solved here: https://stackoverflow.com/questions/23302698/java-check-if-two-rectangles-overlap-at-any-point
        //Keep in mind that for webpages X's are the same but Y is flipped
        //In other words, webpage (0,0) is at the top left of the screen (not bottom left as for typical graph)
        int x1 = (int) MBR_one.get(0);
        int y1 = (int) MBR_one.get(1);
        int x2 = (int) MBR_one.get(2);
        int y2 = (int) MBR_one.get(3);
        int x3 = (int) MBR_two.get(0);
        int y3 = (int) MBR_two.get(1);
        int x4 = (int) MBR_two.get(2);
        int y4 = (int) MBR_two.get(3);
        return (x1 < x4) && (x3 < x2) && (y1 < y4) && (y3 < y2);
    }

}
