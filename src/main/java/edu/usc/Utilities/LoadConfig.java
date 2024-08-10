package edu.usc.Utilities;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class LoadConfig {

    public static Properties prop;

    public static HashMap subject_URLs;

    public LoadConfig() {
        try {
            File configFile = new File("config.txt");
            FileReader reader = new FileReader(configFile);
            Properties new_prop = new Properties();
            new_prop.load(reader);
            prop = new_prop;
            subject_URLs = new HashMap();
            MapSubjectURLs();
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

    public void MapSubjectURLs() throws IOException {
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        File csvFile = new File(prop.getProperty("subject_URLs_location"));
        MappingIterator<String[]> iterator = mapper.readerFor(String[].class).readValues(csvFile);
        while(iterator.hasNext()) {
            String[] row = iterator.next();
            String subject = row[0];
            String URL = row[1];
            if (subject.equals("Subject") && URL.equals("URL")) {
                continue;
            }
            this.subject_URLs.put(subject, URL);
        }
    }

    public String getSubjectURL(String subject){
        return (String) subject_URLs.get(subject);
    }

}
