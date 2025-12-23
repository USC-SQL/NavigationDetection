package edu.usc.Utilities.LNFValidation;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphNode;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.Utilities.LoadConfig;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.io.*;
import java.util.*;

import static edu.usc.KFG.KFGUtilities.UtilityFunctions.LoadTheKFG;

public class ProcessGT {

    public static void ConvertGTXpaths(String subject, LoadConfig configs_obj, String type) throws IOException {
        UIGraphState KFG = LoadTheKFG(configs_obj, subject);
        List<UIGraphEdge> order = KFG.getOrder();
        Map<String, String> GTmapping = new HashMap<>();
        Set<String> GroundTruth = new HashSet();
        Set<String> MappedGT = new HashSet<>();

        //Creating mapping of KFFG to xpaths
        for(UIGraphEdge edge: order){
            UIGraphNode source = edge.getV1();
            UIGraphNode target = edge.getV2();
            String numbered_edge = source.getOrder() + "->" + target.getOrder();
            String xpath_edge = source.getXpath() + " - > " + target.getXpath();
            //System.out.println(numbered_edge);
            //System.out.println(xpath_edge);
            GTmapping.put(numbered_edge, xpath_edge);
        }

        String numbered_entry = "Browser->1";
        String xpath_entry = "Browser - > " + KFG.getV_entry().getXpath();
        String numbered_exit = order.get(order.size()-1).getV2().getOrder() + "->Browser";
        String xpath_exit = order.get(order.size()-1).getV2().getXpath() + " - > Browser";
        GTmapping.put(numbered_entry, xpath_entry);
        GTmapping.put(numbered_exit, xpath_exit);

        //Getting the ground truth failure edges in the numbered edge form
        String file_path = "C:\\Users\\TESTETSSETEST\\Documents\\all_groundtruth\\targetted_gt\\raw_groundtruth\\" + subject + "\\" + type;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file_path));
            String line = "";
            while ((line = reader.readLine()) != null) {
                GroundTruth.add(line.replaceAll("\\s+",""));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Mapping GT to xpaths for developers
        Iterator<String> setIterator = GroundTruth.iterator();
        while(setIterator.hasNext()){
            String edge = setIterator.next();
            String mapped_edge = GTmapping.get(edge);
            if(mapped_edge == null){
                //System.out.println(edge);
            }
            //System.out.println(mapped_edge);
            MappedGT.add(mapped_edge);
        }

        //Writing mapping to txt for develoeprs
        if(type.equals("num_failures.txt")){
            type = "xpath_failures.txt";
        }
        String output_path = "C:\\Users\\TESTSETSE\\Document\\all_groundtruth\\targetted_gt\\processed_groundtruth\\" + subject + "\\" + type;
        Files.createDirectories(Paths.get("C:\\Users\\TESTSETSET\\Documents\\all_groundtruth\\targetted_gt\\processed_groundtruth\\" + subject));
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(output_path), "UTF-8"));
            for (String s : MappedGT) {
                pw.println(s);
            }
            pw.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } finally {
            pw.close();
        }
    }
}
