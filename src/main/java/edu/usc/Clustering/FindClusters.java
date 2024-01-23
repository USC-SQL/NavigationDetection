package edu.usc.Clustering;

import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class FindClusters {

    public static void Cluster(Properties config) throws Exception {
        String subject_URL = config.getProperty("subject_live");
        GetElements elements_object = new GetElements();
        GetVectors vectors_object = new GetVectors();

        List<WebElement> visible_elements = elements_object.AllVisibleElements(subject_URL);
        Map all_vectors = vectors_object.Vectors(visible_elements);
        System.out.println("Total number of feature vectors: " + all_vectors.size());




        elements_object.closedown();

        test();
    }

    public static void test() throws Exception {
        DataSource source = new DataSource("weather.arff");
        Instances traindata = source.getDataSet();
        //traindata.setClassIndex(traindata.numAttributes()-1);

        SimpleKMeans kmeans = new SimpleKMeans();
        kmeans.setNumClusters(4);
        kmeans.buildClusterer(traindata);

        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(kmeans);
        eval.evaluateClusterer(traindata);
        System.out.println(eval.clusterResultsToString());
    }

}
