package edu.usc.WinnTree.Construction;

import ai.onnxruntime.OrtException;
import edu.usc.Utilities.LoadConfig;
import edu.usc.Utilities.ReadJSON;
import edu.usc.Utilities.mitm.GetWebDriver;
import edu.usc.WinnTree.Construction.AffinityCalculation.GetAttributeData;
import edu.usc.WinnTree.Construction.AffinityGraph.AffinityCompleteGraph;
import edu.usc.WinnTree.FunctionalArea;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.*;

public class ConstructWinnTree {

    ChromeDriver Driver;

    public void Construct(LoadConfig configs, String subject) throws OrtException {

        Set<FunctionalArea> work_set = new HashSet<>();
        InitializeWorkSet(work_set, configs, subject);
        AffinityCompleteGraph Affinity_graph = new AffinityCompleteGraph();
        InitializeAffinity_graph(work_set, Affinity_graph);
        int x = 0;
        while(x < 1){
            for(FunctionalArea Source: work_set){
                for(FunctionalArea Target: work_set){

                }
            }
            x++;
        }
    }

    public void InitializeWorkSet(Set work_set, LoadConfig configs, String subject){
        //Finding and getting all xpaths of keyboard-navigable elements
        String subject_path = configs.getProperties().getProperty("KFG_graph_location") + File.separator + subject + File.separator + "KFG.json";
        ReadJSON read_json = new ReadJSON();
        JSONObject subject_JSON = read_json.getJsonObjectFromJsonFile(subject_path);
        List<String> xpaths = read_json.GetXpathsFromKFG(subject_JSON);

        //Getting necessary information from all keyboard-navigable elements
        //Turning them into functional areas
        GetWebDriver WebDriverObj = new GetWebDriver(subject, "https://robinhood.com/login", configs);
        WebDriver refDriver = WebDriverObj.getWebDriver();
        GetAttributeData attribute_obj = new GetAttributeData();
        int ele_count = 1;
        for(String xpath: xpaths){
            WebElement web_ele = refDriver.findElement(By.xpath(xpath));
            ArrayList ele_data = attribute_obj.AttributeData(refDriver, web_ele);
            HashSet ele_css = attribute_obj.getAppliedCSS(refDriver, web_ele);

            Map<String, String> ele_thematic = new HashMap<String, String>();
            ele_thematic.put("id", (String) ele_data.get(0));
            ele_thematic.put("name", (String) ele_data.get(1));
            ele_thematic.put("text", (String) ele_data.get(2));
            List<Integer> MBR = (List<Integer>) ele_data.get(3);
            List centroid = (List) ele_data.get(4);

            FunctionalArea leaf = new FunctionalArea(String.valueOf(ele_count), xpath, "", MBR);
            leaf.setCentroid(centroid);
            leaf.setThematic_info(ele_thematic);
            leaf.setApplied_css(ele_css);

            work_set.add(leaf);
            ele_count++;
        }

        attribute_obj.FilterCSS(work_set);
        System.out.println("Workset size: " + work_set.size());
        WebDriverObj.shutdownWebDriver();

    }

    public void InitializeAffinity_graph(Set work_set, AffinityCompleteGraph Affinity_graph){
        Iterator<FunctionalArea> setIterator = work_set.iterator();
        while(setIterator.hasNext()){
            FunctionalArea FA = setIterator.next();
            Affinity_graph.AddVertex(FA);
        }
    }

}
