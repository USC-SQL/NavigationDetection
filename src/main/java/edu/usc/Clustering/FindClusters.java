package edu.usc.Clustering;

import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FindClusters {

    public static void Cluster(Properties config){
        String subject_URL = config.getProperty("subject_live");
        GetElements elements_object = new GetElements();
        GetVectors vectors_object = new GetVectors();
        List<WebElement> visible_elements = elements_object.AllVisibleElements(subject_URL);
        Map all_vectors = vectors_object.Vectors(visible_elements);
        System.out.println("Total number of feature vectors: " + all_vectors.size());
        elements_object.closedown();
    }

}
