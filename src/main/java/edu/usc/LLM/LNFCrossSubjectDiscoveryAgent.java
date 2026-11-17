package edu.usc.LLM;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LNFCrossSubjectDiscoveryAgent {

    private static final Path BASE = Path.of(
            "Evaluation_Scripts",
            "LNF_Error_Pattern_Discovery"
    );

    private static final Path INPUT =
            BASE.resolve("cross_subject_input.json");

    private static final Path OUTPUT =
            BASE.resolve("cross_subject_patterns.json");

    private static final Path PROMPT = Path.of(
            "src/main/resources/LLM/"
                    + "LNFCrossSubjectDiscovery_prompt.txt"
    );

    private static final int MAX_ATTEMPTS = 2;

    private final OpenAIClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public LNFCrossSubjectDiscoveryAgent(OpenAIClient client) {
        this.client = client;
    }

    public void discover()
            throws IOException, InterruptedException {

        if (Files.exists(OUTPUT)) {
            throw new IOException(
                    "Output already exists: " + OUTPUT);
        }

        JsonNode input = mapper.readTree(
                Files.readString(INPUT, StandardCharsets.UTF_8)
        );

        Map<String, JsonNode> cases = new HashMap<>();

        for (JsonNode item : input.path("error_cases")) {
            String id = item.path("case_id").asText();

            if (cases.put(id, item) != null) {
                throw new IOException(
                        "Duplicate input case ID: " + id);
            }
        }

        if (cases.size() != 66) {
            throw new IOException(
                    "Expected 66 cases, found " + cases.size());
        }

        String template = Files.readString(
                PROMPT, StandardCharsets.UTF_8);

        StringBuilder prompt = new StringBuilder();

        prompt.append(template);
        prompt.append("\n\nCROSS-SUBJECT EVIDENCE DATA:\n");
        prompt.append(
                mapper.writeValueAsString(input)
        );

        String systemPrompt =
                "You are an independent accessibility research "
                        + "analyst discovering cross-subject LNF "
                        + "detection error patterns. "
                        + "Use only supplied evidence. "
                        + "Return valid JSON.";

        for (int attempt = 1;
             attempt <= MAX_ATTEMPTS;
             attempt++) {

            System.out.println(
                    "Cross-subject discovery attempt "
                            + attempt + "/" + MAX_ATTEMPTS);

            String response = client.sendPrompt(
                    systemPrompt,
                    prompt.toString(),
                    List.of()
            );

            try {
                JsonNode result = mapper.readTree(response);

                validate(result, cases);

                Files.writeString(
                        OUTPUT,
                        mapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(result),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW
                );

                printSummary(result, cases);

                System.out.println("\nSaved to: " + OUTPUT);
                return;

            } catch (IOException | IllegalArgumentException e) {
                System.err.println(
                        "Validation failed: " + e.getMessage());

                if (attempt == MAX_ATTEMPTS) {
                    throw new IOException(
                            "Cross-subject discovery failed validation",
                            e);
                }

                prompt.append("\n\nVALIDATION CORRECTION:\n");
                prompt.append(e.getMessage());
                prompt.append(
                        "\nRegenerate the entire JSON response. "
                                + "Use only valid input case IDs. "
                                + "Every case must be covered by a "
                                + "pattern or marked unclassified."
                );
            }
        }
    }

    private void validate(
            JsonNode result,
            Map<String, JsonNode> cases) {

        JsonNode patterns = result.path("patterns");

        if (!patterns.isArray()) {
            throw new IllegalArgumentException(
                    "Missing patterns array");
        }

        Set<String> patternIds = new HashSet<>();
        Set<String> coveredCases = new HashSet<>();

        for (JsonNode pattern : patterns) {

            String id =
                    pattern.path("pattern_id").asText();

            String type =
                    pattern.path("error_type").asText();

            if (!id.matches("(FP|FN)[0-9]{2}")
                    || !id.startsWith(type)) {
                throw new IllegalArgumentException(
                        "Invalid pattern ID/type: " + id);
            }

            if (!patternIds.add(id)) {
                throw new IllegalArgumentException(
                        "Duplicate pattern ID: " + id);
            }

            String[] requiredFields = {
                    "name",
                    "short_summary",
                    "matching_criteria",
                    "hypothesized_cause",
                    "uncertainty"
            };

            for (String field : requiredFields) {
                if (!pattern.path(field).isTextual()
                        || pattern.path(field)
                        .asText().isBlank()) {
                    throw new IllegalArgumentException(
                            "Missing " + field + " in " + id);
                }
            }

            JsonNode matched =
                    pattern.path("matched_case_ids");

            if (!matched.isArray() || matched.size() < 2) {
                throw new IllegalArgumentException(
                        "Pattern must have >=2 cases: " + id);
            }

            Set<String> subjects = new HashSet<>();
            Set<String> localCases = new HashSet<>();

            for (JsonNode item : matched) {

                String caseId = item.asText();

                if (!localCases.add(caseId)) {
                    throw new IllegalArgumentException(
                            "Duplicate membership: "
                                    + id + "/" + caseId);
                }

                JsonNode error = cases.get(caseId);

                if (error == null) {
                    throw new IllegalArgumentException(
                            "Unknown case ID: " + caseId);
                }

                if (!type.equals(
                        error.path("classification").asText())) {
                    throw new IllegalArgumentException(
                            "Wrong error type for "
                                    + id + "/" + caseId);
                }

                subjects.add(
                        error.path("subject").asText());

                coveredCases.add(caseId);
            }

            if (subjects.size() < 2) {
                throw new IllegalArgumentException(
                        "Pattern does not span >=2 subjects: "
                                + id);
            }
        }

        JsonNode unclassified =
                result.path("unclassified_case_ids");

        if (!unclassified.isArray()) {
            throw new IllegalArgumentException(
                    "Missing unclassified_case_ids array");
        }

        Set<String> unclassifiedIds = new HashSet<>();

        for (JsonNode item : unclassified) {
            String id = item.asText();

            if (!cases.containsKey(id)) {
                throw new IllegalArgumentException(
                        "Unknown unclassified ID: " + id);
            }

            if (!unclassifiedIds.add(id)) {
                throw new IllegalArgumentException(
                        "Duplicate unclassified ID: " + id);
            }

            if (coveredCases.contains(id)) {
                throw new IllegalArgumentException(
                        "Case both matched and unclassified: " + id);
            }
        }

        coveredCases.addAll(unclassifiedIds);

        if (!coveredCases.equals(cases.keySet())) {
            Set<String> missing =
                    new HashSet<>(cases.keySet());

            missing.removeAll(coveredCases);

            throw new IllegalArgumentException(
                    "Missing error cases: " + missing);
        }

        if (result.path("overall_summary")
                .asText().isBlank()) {
            throw new IllegalArgumentException(
                    "Missing overall_summary");
        }
    }

    private void printSummary(
            JsonNode result,
            Map<String, JsonNode> cases) {

        System.out.println("\n" + "=".repeat(50));
        System.out.println("DISCOVERED PATTERNS");
        System.out.println("=".repeat(50));

        for (JsonNode pattern : result.path("patterns")) {

            Set<String> subjects = new HashSet<>();

            for (JsonNode id :
                    pattern.path("matched_case_ids")) {

                subjects.add(
                        cases.get(id.asText())
                                .path("subject").asText()
                );
            }

            System.out.printf(
                    "%s [%s] %s%n",
                    pattern.path("pattern_id").asText(),
                    pattern.path("error_type").asText(),
                    pattern.path("name").asText()
            );

            System.out.printf(
                    "  Error cases: %d | Subjects: %d%n",
                    pattern.path("matched_case_ids").size(),
                    subjects.size()
            );
        }

        System.out.println(
                "\nDiscovered patterns: "
                        + result.path("patterns").size());

        System.out.println(
                "Unclassified errors: "
                        + result.path(
                        "unclassified_case_ids").size());
    }

    public static void main(String[] args)
            throws Exception {

        OpenAIClient client =
                new OpenAIClient("gpt-5.4");

        new LNFCrossSubjectDiscoveryAgent(
                client).discover();
    }
}