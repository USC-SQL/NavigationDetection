package edu.usc.ContextTree.Construction;

import edu.usc.Utilities.LoadConfig;
import edu.usc.Utilities.ReadJSON;
import edu.usc.Utilities.mitm.GetWebDriver;
import edu.usc.ContextTree.Construction.AffinityCalculation.GetAttributeData;
import edu.usc.ContextTree.Construction.AffinityGraph.AffinityCompleteGraph;
import edu.usc.ContextTree.FunctionalArea;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static edu.usc.ContextTree.Construction.ConstructParent.GetNewParent;
import static edu.usc.ContextTree.Construction.GroupComparison.IsValidGroup.ValidGroup;

public class ConstructContextTree {

    ChromeDriver Driver;

    public FunctionalArea Construct(LoadConfig configs, String subject) throws InterruptedException, IOException {
        Set<FunctionalArea> work_set = new HashSet<>();
        InitializeWorkSet(work_set, configs, subject);
        System.out.println("Initial size of workset: " + work_set.size());
        AffinityCompleteGraph Affinity_graph = new AffinityCompleteGraph();
        InitializeAffinity_graph(work_set, Affinity_graph);
        Affinity_graph.SortEdgeSet();
        System.out.println("Done with initialization");
        int vertex_count = work_set.size();
        //Loop runs until just the root node is left (ie the full tree is built)
        while(work_set.size() > 1){
            System.out.println("ACG Edge Set Size: " + Affinity_graph.edge_set.size());
            Set<FunctionalArea> children = ValidGroup(configs, work_set, Affinity_graph);
            //removing children from workset & affinity graph
            work_set.removeAll(children);
            for(FunctionalArea child: children){
                Affinity_graph.RemoveVertex(child);
            }
            vertex_count++; //keeps track of nodes in tree & increments to create a new parent node
            FunctionalArea new_parent = GetNewParent(children, vertex_count);
            work_set.add(new_parent); //adding new parent to work set
            Affinity_graph.AddVertex(new_parent); //adding new parent to affinity graph
            Affinity_graph.SortEdgeSet(); //update graph for next iteration
            //System.out.println("New workset size after new grouping: " + work_set.size());
            //System.out.println();
        }
        FunctionalArea root_node = work_set.iterator().next();
        return root_node;
    }

    public void InitializeWorkSet(Set work_set, LoadConfig configs_obj, String subject) throws InterruptedException, IOException {
        //Finding and getting all xpaths of keyboard-navigable elements
        String subject_path = configs_obj.CheckKFGType() + File.separator + subject + File.separator + "KFFG.json";
        ReadJSON read_json = new ReadJSON();
        JSONObject subject_JSON = read_json.getJsonObjectFromJsonFile(subject_path);
        List<String> xpaths = read_json.GetXpathsFromKFG(subject_JSON);
        //Getting necessary information from all keyboard-navigable elements
        //Turning them into functional areas
        GetWebDriver WebDriverObj = new GetWebDriver(subject, configs_obj.getSubjectURL(subject), configs_obj);
        TimeUnit.SECONDS.sleep(10);
        WebDriver refDriver = WebDriverObj.getWebDriver();
        GetAttributeData attribute_obj = new GetAttributeData();
        int ele_count = 1;
        for(String xpath: xpaths){
            WebElement web_ele = refDriver.findElement(By.xpath(xpath));
            ArrayList ele_data = attribute_obj.AttributeData(refDriver, web_ele);
            HashSet<String> ele_css = attribute_obj.getAppliedCSS(refDriver, web_ele);

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
            leaf.setAllThematic_info((String) ele_data.get(5));
            leaf.setBackgroundColor((String) ele_data.get(6));

            work_set.add(leaf);
            ele_count++;
        }
        attribute_obj.FilterCSS(work_set);
        WebDriverObj.shutdownWebDriver();
        WebDriverObj.shutdownMitmProxy();
    }

    public void InitializeAffinity_graph(Set work_set, AffinityCompleteGraph Affinity_graph){
        Iterator<FunctionalArea> setIterator = work_set.iterator();
        while(setIterator.hasNext()){
            FunctionalArea FA = setIterator.next();
            Affinity_graph.AddVertex(FA);
        }
    }

}
