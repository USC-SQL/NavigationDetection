package edu.usc.Utilities;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

import java.io.*;
import java.util.*;

public class LoadConfig {

    public static Properties prop;

    public static HashMap subject_URLs;

    public static List<String> subjects_to_run;

    public LoadConfig() {
        try {
            File configFile = new File("config.txt");
            FileReader reader = new FileReader(configFile);
            Properties new_prop = new Properties();
            new_prop.load(reader);
            prop = new_prop;
            subject_URLs = new HashMap();
            subjects_to_run = new ArrayList<>();
            GetSubjects();
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

    public List<String> getSubjects(){return subjects_to_run;}

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

    public void GetSubjects() throws FileNotFoundException {
        Scanner s = new Scanner(new File("subjects.txt"));
        while (s.hasNextLine()){
            subjects_to_run.add(s.nextLine());
        }
        s.close();
    }

    public String CheckKFGType(){
        /*
        String check = prop.getProperty("use_GT_KFG");
        if(!check.equals("true") && !check.equals("false")){
            System.out.println("ERROR: incorrectly configured CONFIG file.");
            System.out.println("ERROR: property 'use_GT_KFG' must be 'true' or 'false'.");
            throw new java.lang.RuntimeException("Fix config.txt file.");
        }
        if(check.equals("true")){
            return prop.getProperty("GT_KFG_location");
        }else{
            return prop.getProperty("KFG_location");
        }
         */
        return prop.getProperty("KFG_location");
    }

    public String GetCTreePath(String subject){
        /*
        String checkRandom = prop.getProperty("use_Random_WTree");
        String checkGT = prop.getProperty("use_GT_WTree");
        if(!checkRandom.equals("true") && !checkRandom.equals("false")){
            System.out.println("ERROR: incorrectly configured CONFIG file.");
            System.out.println("ERROR: property 'use_Random_WTree' must be 'true' or 'false'.");
            throw new java.lang.RuntimeException("Fix config.txt file.");
        }
        if(!checkGT.equals("true") && !checkGT.equals("false")){
            System.out.println("ERROR: incorrectly configured CONFIG file.");
            System.out.println("ERROR: property 'use_GT_WTree' must be 'true' or 'false'.");
            throw new java.lang.RuntimeException("Fix config.txt file.");
        }
        if(checkRandom.equals("true") && checkGT.equals("true")){
            System.out.println("ERROR: incorrectly configured CONFIG file.");
            System.out.println("ERROR: properties 'use_Random_WTree' and 'use_GT_WTree' cannot both be 'true'.");
            throw new java.lang.RuntimeException("Fix config.txt file.");
        }
        String path = "";
        if(checkRandom.equals("true")){ //using randomly generated WTree
            path = prop.getProperty("WTree_location") + File.separator + subject + File.separator + "WTreeRandom.json";
        }else if(checkGT.equals("true")){ //using author's manually built 'ideal' WTrees
            path = prop.getProperty("GT_WTree_location") + File.separator + subject + File.separator + "WTree.json";
        }else { //using tool's generated WTrees
            path = prop.getProperty("WTree_location") + File.separator + subject + File.separator + "WTree.json";
        }
        return path;
        */
        return prop.getProperty("CTree_location") + File.separator + subject + File.separator + "WTree.json";
    }
}
