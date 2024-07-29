package edu.usc;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import edu.usc.Utilities.LoadConfig;
import edu.usc.Utilities.mitm.GetWebDriver;
import edu.usc.WinnTree.Construction.ConstructWinnTree;
import edu.usc.WinnTree.FunctionalArea;
import edu.usc.WinnTree.WinnTree;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class Main {
    public static void main(String[] args) throws Exception {
        LoadConfig config_obj = new LoadConfig();
        Properties Configs = config_obj.getProperties();
        String subject = Configs.getProperty("subject");
        System.out.println("Subject: " + subject);
        //WinnTree test = new WinnTree();
        //test.Load(subject);

        ConstructWinnTree test = new ConstructWinnTree();
        test.Construct(config_obj, subject);
        System. exit(0);


    }
}