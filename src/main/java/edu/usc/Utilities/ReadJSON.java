package edu.usc.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReadJSON {

    public JSONObject getJsonObjectFromJsonFile(String filePath) {
        File f = new File(filePath);
        InputStream is = null;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // start reading in json information
        JSONTokener tokener = new JSONTokener(is);
        JSONObject kNFGObj = new JSONObject(tokener);

        return kNFGObj;
    }

    public List GetXpathsFromKFG(JSONObject subject_obj){
        List<String> xpaths = new ArrayList<>();
        JSONArray subject_uistates = (JSONArray) subject_obj.get("UIGraphStates");
        JSONObject subject_state = (JSONObject) subject_uistates.get(0);
        JSONArray subject_nodes = (JSONArray) subject_state.get("nodes");
        for (int i = 0; i < subject_nodes.length(); i++){
            JSONObject node = (JSONObject) subject_nodes.get(i);
            String xpath = (String) node.get("xpath");
            xpaths.add(xpath);
        }
        return xpaths;
    }

}
