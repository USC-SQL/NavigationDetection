package edu.usc.LLM;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class LNFPatternMatchingAgent {

    private static final Path BASE = Path.of(
            "Evaluation_Scripts",
            "LNF_Error_Pattern_Discovery"
    );

    private static final Path INPUT_DIR =
            BASE.resolve("matching_inputs");

    private static final Path OUTPUT_DIR =
            BASE.resolve("matching_outputs");

    private static final Path DEFINITIONS =
            BASE.resolve("matching_pattern_definitions.json");

    private static final Path PROMPT = Path.of(
            "src/main/resources/LLM/"
                    + "LNFPatternMatching_prompt.txt"
    );

    private static final int MAX_ATTEMPTS = 3;

    private final OpenAIClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public LNFPatternMatchingAgent(OpenAIClient client) {
        this.client = client;
    }

    public Path analyze(String subject)
            throws IOException, InterruptedException {

        if (!subject.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException(
                    "Invalid subject: " + subject);
        }

        Path inputPath =
                INPUT_DIR.resolve(subject + ".json");

        Path outputPath =
                OUTPUT_DIR.resolve(subject + ".json");

        if (Files.exists(outputPath)) {
            throw new IOException(
                    "Output already exists: " + outputPath);
        }

        String inputText = Files.readString(
                inputPath, StandardCharsets.UTF_8);

        String definitionsText = Files.readString(
                DEFINITIONS, StandardCharsets.UTF_8);

        JsonNode input = mapper.readTree(inputText);
        JsonNode definitions =
                mapper.readTree(definitionsText);

        if (!subject.equals(input.path("subject").asText())) {
            throw new IOException("Input subject mismatch");
        }

        Set<String> patternIds = new HashSet<>();

        JsonNode patterns = definitions.path("patterns");

        if (!patterns.isArray() || patterns.size() != 9) {
            throw new IOException(
                    "Expected nine pattern definitions");
        }

        for (JsonNode pattern : patterns) {
            String id = pattern.path("pattern_id").asText();

            if (!id.matches("P[0-9]{2}")
                    || !patternIds.add(id)) {
                throw new IOException(
                        "Invalid or duplicate pattern ID: " + id);
            }
        }

        List<Path> screenshots = new ArrayList<>();

        JsonNode screenshotPaths =
                input.path("screenshot_paths");

        if (!screenshotPaths.isArray()
                || screenshotPaths.isEmpty()) {
            throw new IOException(
                    "Missing screenshots for " + subject);
        }

        for (JsonNode item : screenshotPaths) {
            Path path = Path.of(item.asText());

            if (!Files.isRegularFile(path)) {
                throw new IOException(
                        "Screenshot not found: " + path);
            }

            screenshots.add(path);
        }

        String template = Files.readString(
                PROMPT, StandardCharsets.UTF_8);

        StringBuilder prompt = new StringBuilder();

        prompt.append(template);

        prompt.append("\n\nPATTERN DEFINITIONS:\n");
        prompt.append(definitionsText);

        prompt.append("\n\nSUBJECT EVIDENCE:\n");
        prompt.append(inputText);

        String systemPrompt =
                "You are an independent keyboard-navigation "
                        + "pattern-matching analyst. "
                        + "Use only the supplied evidence. "
                        + "Do not infer hidden evaluation labels. "
                        + "Return complete valid JSON.";

        for (int attempt = 1;
             attempt <= MAX_ATTEMPTS;
             attempt++) {

            System.out.println(
                    "[" + subject + "] Matching attempt "
                            + attempt + "/" + MAX_ATTEMPTS);

            String response = client.sendPrompt(
                    systemPrompt,
                    prompt.toString(),
                    screenshots
            );

            try {
                JsonNode result = mapper.readTree(response);

                validate(
                        result,
                        input,
                        patternIds,
                        subject
                );

                Files.createDirectories(OUTPUT_DIR);

                Files.writeString(
                        outputPath,
                        mapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(result),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW
                );

                printSummary(result, subject);

                System.out.println(
                        "Saved to: " + outputPath);

                return outputPath;

            } catch (IOException
                     | IllegalArgumentException e) {

                System.err.println(
                        "[" + subject + "] Validation failed: "
                                + e.getMessage());

                if (attempt == MAX_ATTEMPTS) {
                    throw new IOException(
                            "Pattern matching failed for "
                                    + subject,
                            e);
                }

                prompt.append(
                        "\n\nVALIDATION CORRECTION:\n"
                );
                prompt.append(e.getMessage());

                prompt.append(
                        "\nRegenerate the complete JSON. "
                                + "Return one assessment for every "
                                + "candidate transition and exactly "
                                + "one decision per pattern."
                );
            }
        }

        throw new IllegalStateException(
                "Unexpected matching termination");
    }

    private void validate(
            JsonNode result,
            JsonNode input,
            Set<String> patternIds,
            String subject) {

        if (!subject.equals(
                result.path("subject").asText())) {
            throw new IllegalArgumentException(
                    "Output subject mismatch");
        }

        Set<String> expectedTransitions =
                new HashSet<>();

        JsonNode candidates =
                input.path("candidate_transitions");

        if (!candidates.isArray()) {
            throw new IllegalArgumentException(
                    "Missing candidate_transitions");
        }

        for (JsonNode candidate : candidates) {
            String id =
                    candidate.path("transition_id").asText();

            if (id.isBlank()
                    || !expectedTransitions.add(id)) {
                throw new IllegalArgumentException(
                        "Duplicate or invalid input transition: "
                                + id);
            }
        }

        JsonNode assessments =
                result.path("transition_assessments");

        if (!assessments.isArray()) {
            throw new IllegalArgumentException(
                    "Missing transition_assessments array");
        }

        Set<String> seenTransitions =
                new HashSet<>();

        for (JsonNode assessment : assessments) {
            String transitionId =
                    assessment.path("transition_id").asText();

            if (!expectedTransitions.contains(transitionId)) {
                throw new IllegalArgumentException(
                        "Unknown transition ID: " + transitionId);
            }

            if (!seenTransitions.add(transitionId)) {
                throw new IllegalArgumentException(
                        "Duplicate transition: " + transitionId);
            }

            JsonNode matches =
                    assessment.path("matches");

            JsonNode uncertain =
                    assessment.path("uncertain");

            JsonNode noMatch =
                    assessment.path("no_match_pattern_ids");

            if (!matches.isArray()
                    || !uncertain.isArray()
                    || !noMatch.isArray()) {
                throw new IllegalArgumentException(
                        "Missing decision arrays for "
                                + transitionId);
            }

            Set<String> decided = new HashSet<>();

            for (JsonNode item : matches) {
                String id =
                        item.path("pattern_id").asText();

                checkPattern(
                        id, patternIds, decided, transitionId);

                if (!item.path("evidence").isTextual()
                        || item.path("evidence")
                        .asText().isBlank()) {
                    throw new IllegalArgumentException(
                            "Missing match evidence: "
                                    + transitionId + "/" + id);
                }
            }

            for (JsonNode item : uncertain) {
                String id =
                        item.path("pattern_id").asText();

                checkPattern(
                        id, patternIds, decided, transitionId);

                if (!item.path("reason").isTextual()
                        || item.path("reason")
                        .asText().isBlank()) {
                    throw new IllegalArgumentException(
                            "Missing uncertainty reason: "
                                    + transitionId + "/" + id);
                }
            }

            for (JsonNode item : noMatch) {
                if (!item.isTextual()) {
                    throw new IllegalArgumentException(
                            "Invalid no_match entry for "
                                    + transitionId);
                }

                checkPattern(
                        item.asText(),
                        patternIds,
                        decided,
                        transitionId
                );
            }

            if (!decided.equals(patternIds)) {
                Set<String> missing =
                        new HashSet<>(patternIds);

                missing.removeAll(decided);

                throw new IllegalArgumentException(
                        "Missing pattern decisions for "
                                + transitionId + ": " + missing);
            }
        }

        if (!seenTransitions.equals(expectedTransitions)) {
            Set<String> missing =
                    new HashSet<>(expectedTransitions);

            missing.removeAll(seenTransitions);

            throw new IllegalArgumentException(
                    "Missing transition assessments: " + missing);
        }

        if (!result.path("subject_summary").isTextual()
                || result.path("subject_summary")
                .asText().isBlank()) {
            throw new IllegalArgumentException(
                    "Missing subject_summary");
        }

        System.out.println(
                "Validated "
                        + seenTransitions.size()
                        + " transitions × "
                        + patternIds.size()
                        + " patterns for " + subject);
    }

    private static void checkPattern(
            String id,
            Set<String> patternIds,
            Set<String> decided,
            String transitionId) {

        if (!patternIds.contains(id)) {
            throw new IllegalArgumentException(
                    "Unknown pattern: "
                            + transitionId + "/" + id);
        }

        if (!decided.add(id)) {
            throw new IllegalArgumentException(
                    "Duplicate pattern decision: "
                            + transitionId + "/" + id);
        }
    }

    private static void printSummary(
            JsonNode result,
            String subject) {

        int matches = 0;
        int uncertain = 0;

        for (JsonNode assessment :
                result.path("transition_assessments")) {

            matches += assessment.path("matches").size();
            uncertain += assessment.path("uncertain").size();
        }

        System.out.println(
                "\nMATCHING SUMMARY: " + subject);
        System.out.println(
                "Transitions: "
                        + result.path(
                        "transition_assessments").size());
        System.out.println(
                "Pattern matches: " + matches);
        System.out.println(
                "Uncertain decisions: " + uncertain);
    }

    public static void main(String[] args)
            throws Exception {

        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "Usage: LNFPatternMatchingAgent <subject>");
        }

        OpenAIClient client =
                new OpenAIClient("gpt-5.4");

        LNFPatternMatchingAgent agent =
                new LNFPatternMatchingAgent(client);

        agent.analyze(args[0]);
    }
}