package edu.usc.LLM;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validates that Stage 3 analyzes every supplied TP, FP, and FN
 * transition exactly once and preserves its classification.
 */
public final class LNFAnalysisValidator {

    private LNFAnalysisValidator() { }

    private static String normalize(String transition) {
        return transition.replaceAll("\\s+", "");
    }

    public static void validate(
            String response,
            String analysisInputJson) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode input = mapper.readTree(analysisInputJson);
        JsonNode output = mapper.readTree(response);

        JsonNode expected = input.path("transition_analysis_input");
        JsonNode actual = output.path("transition_analysis");
        JsonNode patterns = output.path("subject_patterns");

        if (!expected.isArray()) {
            throw new IllegalArgumentException(
                    "Input is missing transition_analysis_input.");
        }

        if (!actual.isArray()) {
            throw new IllegalArgumentException(
                    "Output is missing transition_analysis.");
        }

        if (!patterns.isArray()) {
            throw new IllegalArgumentException(
                    "Output is missing subject_patterns.");
        }

        String subject = input.path("subject").asText();

        if (!subject.equals(output.path("subject").asText())) {
            throw new IllegalArgumentException(
                    "Subject does not match analysis input.");
        }

        Map<String, String> expectedTransitions = new HashMap<>();

        for (JsonNode entry : expected) {
            String transition =
                    normalize(entry.path("transition").asText());

            String classification =
                    entry.path("classification").asText();

            if (expectedTransitions.putIfAbsent(
                    transition, classification) != null) {
                throw new IllegalArgumentException(
                        "Duplicate transition in input: " + transition);
            }
        }

        Set<String> seen = new HashSet<>();

        for (JsonNode entry : actual) {
            String transition =
                    normalize(entry.path("transition").asText());

            String classification =
                    entry.path("classification").asText();

            if (!expectedTransitions.containsKey(transition)) {
                throw new IllegalArgumentException(
                        "Unknown transition in analysis: " + transition);
            }

            if (!seen.add(transition)) {
                throw new IllegalArgumentException(
                        "Duplicate transition in analysis: " + transition);
            }

            if (!expectedTransitions.get(transition)
                    .equals(classification)) {
                throw new IllegalArgumentException(
                        "Incorrect classification for: " + transition);
            }

            String explanation =
                    entry.path("likely_explanation").asText();

            if (explanation.isBlank()) {
                throw new IllegalArgumentException(
                        "Missing explanation for: " + transition);
            }
        }

        if (seen.size() != expectedTransitions.size()) {
            Set<String> missing =
                    new HashSet<>(expectedTransitions.keySet());

            missing.removeAll(seen);

            throw new IllegalArgumentException(
                    "Analysis omitted " + missing.size()
                            + " transition(s): " + missing);
        }

        System.out.println(
                "Stage 3 analysis validated: "
                        + seen.size() + " transitions.");
    }
}