package edu.usc.Utilities.Detection;

import edu.usc.ContextTree.FunctionalArea;

public class HeuristicUtility {

    public static boolean EdgeHorizontallyAligned(FunctionalArea A, FunctionalArea B){
        Double A_ycoor = Double.valueOf(A.getMBR().get(1));
        Double B_ycoor = Double.valueOf(B.getMBR().get(1));
        double weight = 0.015;
        if(A_ycoor > 2500 && B_ycoor > 2500){
            weight = 0.004;
        }
        if((B_ycoor * (1-weight)) <= A_ycoor && A_ycoor <= (B_ycoor * (1+weight))){
            return true;
        }
        if((A_ycoor * (1-weight)) <= B_ycoor && B_ycoor <= (A_ycoor * (1+weight))){
            return true;
        }
        return false;
    }

    public static boolean EdgeCentroidAligned(FunctionalArea A, FunctionalArea B){
        Double A_ycoor = Double.valueOf((A.getMBR().get(1) + A.getMBR().get(3))/2);
        Double B_ycoor = Double.valueOf((B.getMBR().get(1) + B.getMBR().get(3))/2);
        double weight = 0.012;
        if(A_ycoor > 2500 && B_ycoor > 2500){
            weight = 0.004;
        }
        if((B_ycoor * (1-weight)) <= A_ycoor && A_ycoor <= (B_ycoor * (1+weight))){
            return true;
        }
        if((A_ycoor * (1-weight)) <= B_ycoor && B_ycoor <= (A_ycoor * (1+weight))){
            return true;
        }
        return false;
    }

    public static boolean EdgeVerticallyAligned(FunctionalArea A, FunctionalArea B){
        Double A_xcoor = Double.valueOf(A.getMBR().get(0));
        Double B_xcoor = Double.valueOf(B.getMBR().get(0));
        if((B_xcoor * 0.98) <= A_xcoor && A_xcoor <= (B_xcoor * 1.02)){
            return true;
        }
        if((A_xcoor * 0.98) <= B_xcoor && B_xcoor <= (A_xcoor * 1.02)){
            return true;
        }
        return false;
    }
}
