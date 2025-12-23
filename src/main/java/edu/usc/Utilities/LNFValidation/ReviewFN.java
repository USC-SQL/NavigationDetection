package edu.usc.Utilities.LNFValidation;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphNode;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.Utilities.LoadConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static edu.usc.KFG.KFGUtilities.UtilityFunctions.LoadTheKFG;

public class ReviewFN {

    public static void PrintFNs(String subject, LoadConfig configs_obj) throws IOException {
        UIGraphState KFG = LoadTheKFG(configs_obj, subject);
        List<UIGraphEdge> order = KFG.getOrder();
        Map<String, String> GTmapping = new HashMap<>();
        Set<String> FNs = new HashSet<>();


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
        String xpath_entry = "Browser->" + KFG.getV_entry().getXpath();
        String numbered_exit = order.get(order.size()-1).getV2().getOrder() + "->Browser";
        String xpath_exit = order.get(order.size()-1).getV2().getXpath() + "->Browser";
        GTmapping.put(numbered_entry, xpath_entry);
        GTmapping.put(numbered_exit, xpath_exit);

        //reading FNs
        String filename = "C:\\Users\\TESTSET\\Documents\\LNFProject\\Targetted_GT_Results\\LYNX-P\\FNs\\" + subject + ".txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                FNs.add(line.trim()); // trim removes extra spaces/newlines
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> inverted = new HashMap<>();
        for (Map.Entry<String, String> entry : GTmapping.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String new_value = value.replaceAll("\\s+", "");
            if (inverted.containsKey(value)) {
                System.out.println("Warning: Value '" + value +
                        "' already mapped to key '" + inverted.get(value) +
                        "'. Overriding with key '" + key + "'.");
            }
            inverted.put(new_value, key);
        }

        for (String FN: FNs){
            String conversion = inverted.get(FN);
            System.out.println(conversion);
        }

    }

}
