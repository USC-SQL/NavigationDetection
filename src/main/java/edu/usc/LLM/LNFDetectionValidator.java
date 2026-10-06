package edu.usc.LLM;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Validates that every LNF reported by the LLM represents an actual
 * consecutive transition in the implemented keyboard navigation path.
 */
public final class LNFDetectionValidator {

    private LNFDetectionValidator() { }

    public static void validate(
            String response,
            String navigationOrderJson) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode responseRoot = objectMapper.readTree(response);
        JsonNode lnfAnalysis = responseRoot.get("lnf_analysis");

        if (lnfAnalysis == null || !lnfAnalysis.isArray()) {
            throw new IllegalArgumentException(
                    "Response does not contain an lnf_analysis array.");
        }

        JsonNode navigationRoot =
                objectMapper.readTree(navigationOrderJson);

        JsonNode navigationOrder =
                navigationRoot.get("navigation_order");

        if (navigationOrder == null || !navigationOrder.isArray()) {
            throw new IllegalArgumentException(
                    "Navigation JSON does not contain a navigation_order array.");
        }

        Map<String, Integer> xpathToLabel = new HashMap<>();

        for (JsonNode entry : navigationOrder) {
            int label = entry.path("label").asInt(-1);
            String xpath = entry.path("xpath").asText("");

            if (label < 1 || xpath.isBlank()) {
                throw new IllegalArgumentException(
                        "Invalid label or XPath in navigation JSON.");
            }

            xpathToLabel.put(xpath, label);
        }

        for (JsonNode entry : lnfAnalysis) {
            String transition =
                    entry.path("transition").asText("");

            String reasoning =
                    entry.path("reasoning").asText("");

            if (transition.isBlank()) {
                throw new IllegalArgumentException(
                        "LNF entry is missing a transition.");
            }

            if (reasoning.isBlank()) {
                throw new IllegalArgumentException(
                        "LNF entry is missing reasoning.");
            }

            String[] xpaths = transition.split("\\s*->\\s*");

            if (xpaths.length != 2) {
                throw new IllegalArgumentException(
                        "Invalid LNF transition format: " + transition);
            }

            String firstXpath = xpaths[0].trim();
            String secondXpath = xpaths[1].trim();

            Integer firstLabel = xpathToLabel.get(firstXpath);
            Integer secondLabel = xpathToLabel.get(secondXpath);

            if (firstLabel == null || secondLabel == null) {
                throw new IllegalArgumentException(
                        "LNF transition contains an unknown XPath: "
                                + transition);
            }

            if (secondLabel != firstLabel + 1) {
                throw new IllegalArgumentException(
                        "LNF transition is not between consecutive "
                                + "implemented navigation labels: "
                                + transition);
            }
        }

        System.out.println(
                "LNF detection validated successfully: "
                        + lnfAnalysis.size()
                        + " suspected LNF(s).");
    }
}