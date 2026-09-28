package edu.usc.LLM;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates that an inferred navigation order contains every KFG
 * element exactly once and preserves the correct label-to-XPath mapping.
 */
public final class NavigationOrderValidator {

    private NavigationOrderValidator() { }

    public static void validate(
            String response,
            List<String> elements) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response);
        JsonNode navigationOrder = root.get("navigation_order");

        if (navigationOrder == null || !navigationOrder.isArray()) {
            throw new IllegalArgumentException(
                    "Response does not contain a navigation_order array.");
        }

        if (navigationOrder.size() != elements.size()) {
            throw new IllegalArgumentException(
                    "Expected " + elements.size()
                            + " navigation elements, but received "
                            + navigationOrder.size() + ".");
        }

        Set<Integer> seenLabels = new HashSet<>();
        Set<Integer> seenPositions = new HashSet<>();

        for (JsonNode entry : navigationOrder) {
            int position = entry.path("position").asInt(-1);
            int label = entry.path("label").asInt(-1);
            String xpath = entry.path("xpath").asText("");

            if (position < 1 || position > elements.size()) {
                throw new IllegalArgumentException(
                        "Invalid navigation position: " + position);
            }

            if (!seenPositions.add(position)) {
                throw new IllegalArgumentException(
                        "Duplicate navigation position: " + position);
            }

            if (label < 1 || label > elements.size()) {
                throw new IllegalArgumentException(
                        "Invalid KFG label: " + label);
            }

            if (!seenLabels.add(label)) {
                throw new IllegalArgumentException(
                        "Duplicate KFG label: " + label);
            }

            String expectedXpath = elements.get(label - 1);

            if (!expectedXpath.equals(xpath)) {
                throw new IllegalArgumentException(
                        "XPath mismatch for label " + label
                                + ". Expected: " + expectedXpath
                                + " Received: " + xpath);
            }
        }

        System.out.println(
                "Navigation order validated successfully: "
                        + elements.size()
                        + " unique elements.");
    }
}
