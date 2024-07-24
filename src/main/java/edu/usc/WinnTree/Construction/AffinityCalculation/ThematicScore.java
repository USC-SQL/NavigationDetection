package edu.usc.WinnTree.Construction.AffinityCalculation;

import ai.onnxruntime.OrtException;
import edu.usc.WinnTree.FunctionalArea;

import java.util.Map;
import java.util.Map.Entry;

public class ThematicScore {

    public static double CalculateThematicScore(FunctionalArea vertexOne, FunctionalArea vertexTwo, SentenceComparer sentenceComparer_obj) {
        Map vertexOne_info = vertexOne.getThematic_info();
        Map vertexTwo_info = vertexTwo.getThematic_info();
        int attribute_count = vertexOne_info.size();
        double thematic_score = 0.0;
        /*
        //Doing an NxN web element attribute comparison
        //For each attribute in v1, we review it against all the other attributes in v2
        //Since both sets are made up of the same attributes, its equivalent to an NxN
        //For each attribute in v1, we then average the afinity for the said attribute
        //After all this we add up all 3 scores (ie 3 attributes) and divide this by 3
        //Dividing by 3 allows the final number to be in the range of {0 ... 1}
        */
        for (Object e_1 : vertexOne_info.entrySet()) {
            double total_affinity = 0;
            Entry entryOne = (Entry) e_1;
            String valueOne = (String) entryOne.getValue();
            for (Object e_2 : vertexTwo_info.entrySet()) {
                Entry entryTwo = (Entry) e_2;
                String valueTwo = (String) entryTwo.getValue();
                double similarity = 0;
                try {
                    similarity = sentenceComparer_obj.CompareSentences(valueOne, valueTwo);
                } catch (OrtException e){
                    System.out.println("problem");
                }
                total_affinity += similarity;
                attribute_count++;
            }
            thematic_score += (total_affinity / attribute_count);
        }

        return (thematic_score / attribute_count);
    }
}
