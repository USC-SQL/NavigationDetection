package edu.usc.WinnTree.Construction.GroupComparison;

import edu.usc.WinnTree.FunctionalArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CheckMBR {

    //Returns true if the potential grouping of nodes (ie FAs) doesn't overlap any others in the workset
    //Returns false if an overlap of MBRs occurs
    public static boolean ChecktheMBR(Set<FunctionalArea> potential_group, Set<FunctionalArea> workset){
        Set check_set = workset;
        check_set.removeAll(potential_group);
        List potential_group_mbr = FindMBR(potential_group);
        for(FunctionalArea FA: workset){
            List FA_mbr = FA.getMBR();
            if(OverlappingMBR(potential_group_mbr, FA_mbr)) {
                return false;
            }
        }
        return true;
    }

    public static List FindMBR(Set<FunctionalArea> group){
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
        int MBR_one_x1 = (int) MBR_one.get(0);
        int MBR_one_y1 = (int) MBR_one.get(1);
        int MBR_one_x2 = (int) MBR_one.get(2);
        int MBR_one_y2 = (int) MBR_one.get(3);
        int MBR_two_x1 = (int) MBR_two.get(0);
        int MBR_two_y1 = (int) MBR_two.get(1);
        int MBR_two_x2 = (int) MBR_two.get(2);
        int MBR_two_y2 = (int) MBR_two.get(3);
        if ((MBR_one_x1 > MBR_two_x2) ||
                (MBR_one_y1 < MBR_two_y2) ||
                (MBR_two_x1 > MBR_one_x2) ||
                (MBR_two_y1 < MBR_one_y2)){
            return false;
        }
        return true;
    }

}
