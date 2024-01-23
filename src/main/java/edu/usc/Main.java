package edu.usc;

import java.util.Properties;
import edu.usc.Utilities.LoadConfig;
import edu.usc.Clustering.FindClusters;

public class Main {
    public static void main(String[] args) {
        Properties Configs = LoadConfig.Config();
        System.out.println("Subject: " + Configs.getProperty("subject_live"));
        FindClusters cluster = new FindClusters();
        cluster.Cluster(Configs);
    }
}