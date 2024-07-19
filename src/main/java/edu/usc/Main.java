package edu.usc;

import java.util.Properties;
import edu.usc.Utilities.LoadConfig;
import edu.usc.WinnTree.WinnTree;

public class Main {
    public static void main(String[] args) throws Exception {
        Properties Configs = LoadConfig.Config();
        System.out.println("Subject: " + Configs.getProperty("subject"));
        String subject = Configs.getProperty("subject");
        WinnTree test = new WinnTree();
        test.Load(subject);
        //WinnTree myree = test.Load(subject);
    }
}