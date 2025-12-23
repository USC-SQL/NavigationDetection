package edu.usc.ContextTree.Construction.AffinityCalculation;

import edu.usc.ContextTree.FunctionalArea;

import java.util.ArrayList;
import java.util.List;

import static edu.usc.ContextTree.Construction.AffinityCalculation.StylisticScore.CalculateStylisticScore;
import static edu.usc.ContextTree.Construction.AffinityCalculation.ThematicScore.CalculateThematicScore;
import static edu.usc.ContextTree.Construction.AffinityCalculation.LocationScore.CalculateVisualScore;

public class AffinityScore{

    public static List<Double> CalculateAffinityScore(FunctionalArea vertexOne, FunctionalArea vertexTwo, SentenceComparer sentenceComparer_obj) {

        double location_score = 1.5 * CalculateVisualScore(vertexOne, vertexTwo);
        //double visual_score = 0;
        double thematic_score = CalculateThematicScore(vertexOne, vertexTwo, sentenceComparer_obj);
        //double thematic_score = 0;
        double stylistic_score = 3.5 * CalculateStylisticScore(vertexOne, vertexTwo);
        //double stylistic_score = 0;

        List<Double> affinity_scores = new ArrayList<>();
        affinity_scores.add(location_score);
        affinity_scores.add(thematic_score);
        affinity_scores.add(stylistic_score);
        //double affinity_score = visual_score + thematic_score + stylistic_score;
        return affinity_scores;
    }

}
