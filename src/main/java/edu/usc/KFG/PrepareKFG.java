package edu.usc.KFG;

import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.Utilities.LoadConfig;

import java.io.File;
import java.util.List;

public class LoadKFG {

    public static UIGraphState LoadTheKFG(LoadConfig configs_obj, String subject){
        String KFG_Dir = configs_obj.getProperties().getProperty("KFG_graph_location") + File.separator + subject + File.separator;
        ReadKFFGFromJSON readKFFGState = new ReadKFFGFromJSON(KFG_Dir + "KFG" + ".json", "KFFG");
        List<UIGraphState> finStateTotalKFFG = readKFFGState.getUIGraphStateList();
        UIGraphState initState = finStateTotalKFFG.get(0);
        return initState;
    }
}
