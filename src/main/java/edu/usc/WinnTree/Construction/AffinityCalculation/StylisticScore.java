package edu.usc.WinnTree.Construction.AffinityCalculation;

import edu.usc.WinnTree.FunctionalArea;

import java.util.HashSet;
import java.util.Objects;

public class StylisticScore {

    public static double CalculateStylisticScore(FunctionalArea vertexOne, FunctionalArea vertexTwo){

        HashSet vertexOne_css = vertexOne.getApplied_css();
        HashSet vertexTwo_css = vertexTwo.getApplied_css();
        HashSet intersection = getIntersection(vertexOne_css, vertexTwo_css);
        double CSS_similarity = (double) intersection.size() / (vertexOne_css.size() + vertexTwo_css.size() - intersection.size());

        double alignment = 0.0;
        int leftAligned = isAligned(vertexOne.getMBR().get(0), vertexTwo.getMBR().get(0));
        int topAligned = isAligned(vertexOne.getMBR().get(1), vertexTwo.getMBR().get(1));
        int rightAligned = isAligned(vertexOne.getMBR().get(2), vertexTwo.getMBR().get(2));
        int bottomAligned = isAligned(vertexOne.getMBR().get(3), vertexTwo.getMBR().get(3));
        int totalAlignment = leftAligned + topAligned + rightAligned + bottomAligned;
        if(totalAlignment == 0) {
            alignment = 0;
        } else if(totalAlignment == 1){
            alignment = 0.5;
        } else {
            alignment = 1;
        }

        //divide by 2 becaues both variables have ranges between {0 ... 1}
        //we need the final similarity value to have range of {0 ... 1} to be consistent with other types
        double stylistic_similarity = (CSS_similarity + alignment) / 2;
        return stylistic_similarity;
    }

    public static HashSet getIntersection(HashSet vertexOne_css, HashSet vertexTwo_css){
        HashSet intersection = new HashSet(vertexOne_css);
        intersection.retainAll(vertexTwo_css);
        return intersection;
    }

    public static int isAligned(int coordA, int coordB){
        if((coordB * 0.95) <= coordA && coordA <= (coordB * 1.05)){
            return 1;
        }
        if((coordA * 0.95) <= coordB && coordB <= (coordA * 1.05)){
            return 1;
        }
        return 0;
    }

}
