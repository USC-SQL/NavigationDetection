package edu.usc.Utilities;

import java.io.*;
import java.util.Properties;

public class LoadConfig {

    public static Properties Config() {
        try {
            String configFilePath = "config.txt";
            FileInputStream propsInput = new FileInputStream(configFilePath);
            Properties prop = new Properties();
            prop.load(propsInput);
            return prop;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
