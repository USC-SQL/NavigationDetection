package edu.usc.WinnTree.Construction.AffinityCalculation;

import ai.onnxruntime.OrtException;
import edu.usc.WinnTree.FunctionalArea;

import static edu.usc.WinnTree.Construction.AffinityCalculation.StylisticScore.CalculateStylisticScore;
import static edu.usc.WinnTree.Construction.AffinityCalculation.ThematicScore.CalculateThematicScore;
import static edu.usc.WinnTree.Construction.AffinityCalculation.VisualScore.CalculateVisualScore;

public class AffinityScore{

    public static double CalculateAffinityScore(FunctionalArea vertexOne, FunctionalArea vertexTwo, SentenceComparer sentenceComparer_obj) throws OrtException {

        double visual_score = CalculateVisualScore(vertexOne, vertexTwo);
        double thematic_score = CalculateThematicScore(vertexOne, vertexTwo, sentenceComparer_obj);
        double stylistic_score = CalculateStylisticScore(vertexOne, vertexTwo);

        double affinity_score = visual_score + thematic_score + stylistic_score;
        return affinity_score;
    }

}
