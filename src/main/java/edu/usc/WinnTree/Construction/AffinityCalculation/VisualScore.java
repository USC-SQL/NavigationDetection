package edu.usc.WinnTree.Construction.AffinityCalculation;

import edu.usc.WinnTree.FunctionalArea;

import java.util.List;

public class VisualScore {

    public static double CalculateVisualScore(FunctionalArea vertexOne, FunctionalArea vertexTwo){
        List<Float> vertexOne_centroid = vertexOne.getCentroid();
        List<Float> vertexTwo_centroid = vertexTwo.getCentroid();
        double hypontenuse = Math.hypot(vertexOne_centroid.get(0) - vertexTwo_centroid.get(0), vertexOne_centroid.get(1) - vertexTwo_centroid.get(1));

        //Need to scale visual distance to {0 .... 1)
        double scaled_distance = hypontenuse / 1999;

        //Small the distance = higher the score awarded
        double visual_score = 1 - scaled_distance;
        return visual_score;
    }

}
