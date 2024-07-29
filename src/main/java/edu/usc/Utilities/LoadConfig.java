package edu.usc.Utilities;

import java.io.*;
import java.util.Properties;

public class LoadConfig {

    public static Properties prop;

    public LoadConfig() {
        try {
            String configFilePath = "config.txt";
            //FileInputStream propsInput = new FileInputStream(configFilePath);
            File configFile = new File("config.txt");
            FileReader reader = new FileReader(configFile);
            Properties new_prop = new Properties();
            new_prop.load(reader);
            prop = new_prop;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties(){
        return prop;
    }

    public String GetmitmPath (){
        String mitmproxyFolderName = prop.getProperty("mitmproxy_folder");
        String resourcesDirectory = new File("src/main/resources").getAbsolutePath();
        String mitmProxy530Basepath = resourcesDirectory + File.separator + mitmproxyFolderName;
        return mitmProxy530Basepath;
    }

    public String GetSubjectPath(String subject){
        String resourcesDirectory = prop.getProperty("cached_subjects_location");
        return resourcesDirectory + File.separator + subject;
    }

}
