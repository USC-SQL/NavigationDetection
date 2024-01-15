package edu.usc;

import edu.usc.Utilities.LoadConfig;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Properties Configs = LoadConfig.main();
        System.out.println(Configs.getProperty("subject"));
    }
}