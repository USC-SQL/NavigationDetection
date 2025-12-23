package edu.usc.ContextTree.Construction.AffinityCalculation;

import edu.usc.ContextTree.FunctionalArea;

import java.util.List;

public class LocationScore {

    public static double CalculateVisualScore(FunctionalArea vertexOne, FunctionalArea vertexTwo){
        List<Double> vertexOne_centroid = vertexOne.getCentroid();
        List<Double> vertexTwo_centroid = vertexTwo.getCentroid();
        double hypontenuse = Math.hypot(vertexOne_centroid.get(0) - vertexTwo_centroid.get(0), vertexOne_centroid.get(1) - vertexTwo_centroid.get(1));

        //Need to scale visual distance to {0 .... 1)
        //Note that it's possible for a webpage to have higher values than the viewport
        double max_distance = Math.hypot(0 - 1280, 0 - 1024); //distance from (0,0) to the viewport
        double scaled_distance = hypontenuse / max_distance;

        //Small the distance = higher the score awarded
        double visual_score = 1 - scaled_distance;
        return visual_score;
    }

}
