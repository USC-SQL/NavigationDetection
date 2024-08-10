package edu.usc;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import edu.usc.Detection.RunDetection;
import edu.usc.Utilities.LoadConfig;
import edu.usc.Utilities.mitm.GetWebDriver;
import edu.usc.WinnTree.Construction.ConstructWinnTree;
import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static edu.usc.Detection.RunDetection.Detect;


public class Main {
    public static void main(String[] args) throws Exception {
        LoadConfig config_obj = new LoadConfig();
        Properties Configs = config_obj.getProperties();
        String subject = Configs.getProperty("subject");
        System.out.println("Subject: " + subject);
        System.out.println("Subject URL: " + config_obj.getSubjectURL(subject));
        /*
        long startTime = System.nanoTime();
        WinnTree test = new WinnTree();
        test.Build(config_obj, subject);
        test.Save(config_obj, subject);
        long endTime = System.nanoTime();
        long duration = ((endTime - startTime) / 1000000 / 1000);
        //System.out.println("Winn Tree Timing: " + duration);
        //ConstructWinnTree test = new ConstructWinnTree();
        //test.Construct(config_obj, subject);

         */
        Detect(config_obj, subject);


        System.exit(0);


    }
}