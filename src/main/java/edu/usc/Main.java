package edu.usc;

import edu.usc.Utilities.LoadConfig;
import edu.usc.MitmProxy;
import java.util.Properties;


//test

import org.openqa.selenium.firefox.FirefoxDriver;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        Properties Configs = LoadConfig.main();
        System.out.println(Configs.getProperty("subject"));

        MitmProxy test = new MitmProxy();
        test.RunSubject(Configs);
    }
    
}