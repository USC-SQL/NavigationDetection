package edu.usc;

import java.util.Properties;
import edu.usc.Utilities.LoadConfig;
import edu.usc.Utilities.mitm.GetWebDriver;
import edu.usc.WinnTree.WinnTree;

public class Main {
    public static void main(String[] args) throws Exception {
        LoadConfig config_obj = new LoadConfig();
        Properties Configs = config_obj.getProperties();
        String subject = Configs.getProperty("subject");
        System.out.println("Subject: " + subject);
        //WinnTree test = new WinnTree();
        //test.Load(subject);

        GetWebDriver test = new GetWebDriver("robinhood", "https://robinhood.com/login", config_obj);
        test.shutdownWebDriver();
    }
}