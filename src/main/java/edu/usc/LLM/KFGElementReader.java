package edu.usc.LLM;

import edu.usc.LYNX.KFG.UIGraph.UIGraphEdge;
import edu.usc.LYNX.KFG.UIGraph.UIGraphState;
import edu.usc.Utilities.LoadConfig;

import java.util.ArrayList;
import java.util.List;

import static edu.usc.LYNX.Helper.LNFValidation.DrawLNFLabels.NormalizeXpath;
import static edu.usc.LYNX.KFG.KFGUtilities.UtilityFunctions.LoadTheKFG;

/**
 * Reads the KFG for a subject and extracts the keyboard-navigable
 * elements in their implemented traversal sequence.
 */
public final class KFGElementReader {

    private KFGElementReader() { }

    public static List<String> readElements(LoadConfig config, String subject) {
        UIGraphState kfg = LoadTheKFG(config, subject);

        if (kfg == null) {
            throw new IllegalStateException(
                    "Unable to load KFG for subject: " + subject);
        }

        List<String> elements = new ArrayList<>();

        // Element 1 is the KFG entry element.
        elements.add(NormalizeXpath(kfg.getV_entry().getXpath()));

        // Remaining numbered elements follow the TAB traversal.
        for (UIGraphEdge edge : kfg.getOrder()) {
            elements.add(NormalizeXpath(edge.getV2().getXpath()));
        }

        return elements;
    }
}