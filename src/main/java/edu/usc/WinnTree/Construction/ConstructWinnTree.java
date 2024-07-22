package edu.usc.WinnTree.Construction;

import edu.usc.Utilities.LoadConfig;
import edu.usc.Utilities.ReadJSON;
import edu.usc.WinnTree.Construction.AffinityGraph.AffinityCompleteGraph;
import edu.usc.WinnTree.FunctionalArea;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ConstructWinnTree {

    ChromeDriver Driver;

    public void Construct(LoadConfig configs, String subject){

        Set<FunctionalArea> work_set = new HashSet<>();
        InitializeWorkSet(work_set, configs, subject);
        AffinityCompleteGraph Affinity_graph = new AffinityCompleteGraph();
        InitializeDistance_graph(work_set, Affinity_graph);

    }

    public void InitializeWorkSet(Set work_set, LoadConfig configs, String subject){
        String subject_path = configs.getProperties().getProperty("KFG_graph_location") + File.separator + subject + File.separator + "KFG.json";
        ReadJSON read_json = new ReadJSON();
        JSONObject subject_JSON = read_json.getJsonObjectFromJsonFile(subject_path);
        List<String> xpaths = read_json.GetXpathsFromKFG(subject_JSON);
        for(String xpath: xpaths){
            System.out.println(xpath);
        }

    }

    public void InitializeDistance_graph(Set work_set, AffinityCompleteGraph Affinity_graph){
        Iterator<FunctionalArea> setIterator = work_set.iterator();
        while(setIterator.hasNext()){
            FunctionalArea FA = setIterator.next();
            Affinity_graph.AddVertex(FA);
        }
    }

}
