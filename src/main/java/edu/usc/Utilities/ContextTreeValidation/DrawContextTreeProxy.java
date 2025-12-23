package edu.usc.Utilities.ContextTreeValidation;

import edu.usc.Utilities.ReadInJSResource;
import edu.usc.Utilities.LoadConfig;
import edu.usc.Utilities.mitm.GetWebDriver;
import edu.usc.ContextTree.FunctionalArea;
import edu.usc.ContextTree.ContextTree;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

///////////////////////
// This class is used to draw the MBRs of all Functional Areas in a provided Context-tree
// It is used as a part of the validation aspect of the approach by having volunteers rate how 'well'
// a given Context Tree segements a web page. Three options: our 'ideal' version, the approach's
// generated version, and a random group version.
///////////////////////
public class DrawContextTreeProxy {

    public static void DrawMBRs(String subject, LoadConfig configs_obj) throws InterruptedException, IOException {
        ContextTree WTree = new ContextTree();
        WTree.Load(configs_obj, subject);
        Set<FunctionalArea> vertex_set = WTree.getVertexSet();
        Set<List<Integer>> drawn_MBRs = new HashSet<>();

        GetWebDriver WebDriverObj = new GetWebDriver(subject, configs_obj.getSubjectURL(subject), configs_obj);
        TimeUnit.SECONDS.sleep(10);
        WebDriver refDriver = WebDriverObj.getWebDriver();

        //Finds the WTree depth of each FA in tree
        int max_depth = 0;
        Map<FunctionalArea, Integer> FA_Depth = new HashMap<>();
        for(FunctionalArea FA: vertex_set){
            List<FunctionalArea> path = WTree.FindDFSPath(FA);
            FA_Depth.put(FA, path.size());
            if(path.size() > max_depth){
                max_depth = path.size();
            }
        }

        //Draws the MBRs of all the WTree leaf nodes (ie keyboard-navigable elements)
        String array = "";
        for(FunctionalArea FA: vertex_set){
            if(!FA.getXpath().isEmpty()){
                List MBR = FA.getMBR();
                int x = (int) MBR.get(0);
                int y = (int) MBR.get(1);
                int width = (int) MBR.get(2) - (int) MBR.get(0);
                int height = (int) MBR.get(3) - (int) MBR.get(1);
                array += x + "," + y + "," + width + "," + height + ";";
                drawn_MBRs.add(MBR);
            }
        }
        int thickness = 2;
        String color = "red";
        ReadInJSResource obj = new ReadInJSResource();
        String script = obj.read(obj, "drawMBR.js");
        ((JavascriptExecutor) refDriver).executeScript(script, array, thickness, color);


        //Sorts the FA based on their WTree depth so we can draw the deeper ones first
        LinkedHashMap<FunctionalArea, Integer> sorted_FA = FA_Depth.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        //Calculates the new MBRs of all FA and draws them
        for(FunctionalArea FA: sorted_FA.keySet()){
            if(FA.getXpath().isEmpty()){
                List<Integer> MBR = FA.getMBR();
                MBR = getOffsetMBR(MBR, drawn_MBRs);
                int width = (int) MBR.get(2) - (int) MBR.get(0);
                int height = (int) MBR.get(3) - (int) MBR.get(1);
                int x = (int) MBR.get(0);
                if(x < 0){x = 0;}
                int y = (int) MBR.get(1);
                if(y < 0){y = 0;}
                array += x + "," + y + "," + width + "," + height + ";";
                drawn_MBRs.add(MBR);
            }
        }

        thickness = 2;
        color = "blue";
        ((JavascriptExecutor) refDriver).executeScript(script, array, thickness, color);
    }

    public static List<Integer> getOffsetMBR(List<Integer> MBR, Set<List<Integer>> drawn_MBRs){
        List<Integer> changed_MBR = new ArrayList<>();
        changed_MBR.add(MBR.get(0));
        changed_MBR.add(MBR.get(1));
        changed_MBR.add(MBR.get(2));
        changed_MBR.add(MBR.get(3));
        for(List drawn_mbr: drawn_MBRs){
            if(changed_MBR.get(0).equals(drawn_mbr.get(0))){
                int x = changed_MBR.get(0);
                x -= 8;
                changed_MBR.set(0, x);
            }
            if(changed_MBR.get(1).equals(drawn_mbr.get(1))){
                int y = changed_MBR.get(1);
                y -= 8;
                changed_MBR.set(1, y);
            }
            if(changed_MBR.get(2).equals(drawn_mbr.get(2))){
                int x2 = changed_MBR.get(2);
                x2 += 8;
                changed_MBR.set(2, x2);
            }
            if(changed_MBR.get(3).equals(drawn_mbr.get(3))){
                int y2 = changed_MBR.get(3);
                y2 += 8;
                changed_MBR.set(3, y2);
            }
        }
        if(!changed_MBR.equals(MBR)){
            changed_MBR = getOffsetMBR(changed_MBR, drawn_MBRs);
        }
        return changed_MBR;
    }
}
