package edu.usc.LLM;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LNFErrorPatternDiscoveryAgent {

    private static final Path BASE = Path.of(
            "Evaluation_Scripts",
            "LNF_Error_Pattern_Discovery"
    );

    private static final Path INPUT_DIR =
            BASE.resolve("subject_inputs");

    private static final Path OUTPUT_DIR =
            BASE.resolve("subject_analysis");

    private static final Path PROMPT_PATH = Path.of(
            "src/main/resources/LLM/"
                    + "LNFErrorPatternDiscovery_prompt.txt"
    );

    private static final int MAX_ATTEMPTS = 3;

    private final OpenAIClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public LNFErrorPatternDiscoveryAgent(
            OpenAIClient client) {
        this.client = client;
    }

    public Path analyze(String subject)
            throws IOException, InterruptedException {

        if (!subject.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException(
                    "Invalid subject name: " + subject);
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

        JsonNode input = mapper.readTree(inputText);

        if (!subject.equals(
                input.path("subject").asText())) {
            throw new IOException(
                    "Subject mismatch in input JSON");
        }

        List<Path> screenshots = new ArrayList<>();

        JsonNode screenshotPaths =
                input.path("screenshot_paths");

        if (!screenshotPaths.isArray()
                || screenshotPaths.isEmpty()) {
            throw new IOException(
                    "Missing screenshot paths for " + subject);
        }

        for (JsonNode item : screenshotPaths) {
            Path image = Path.of(item.asText());

            if (!Files.isRegularFile(image)) {
                throw new IOException(
                        "Screenshot not found: " + image);
            }

            screenshots.add(image);
        }

        String template = Files.readString(
                PROMPT_PATH, StandardCharsets.UTF_8);

        StringBuilder prompt = new StringBuilder();

        prompt.append(template);
        prompt.append("\n\nSUBJECT EVIDENCE PACKAGE:\n");
        prompt.append(inputText);

        String systemPrompt =
                "You are an independent accessibility "
                        + "research analyst investigating "
                        + "LNF detection errors under WCAG 2.4.3. "
                        + "Analyze supplied evidence carefully. "
                        + "Preserve exact identifiers and "
                        + "evaluation labels. Return valid JSON.";

        for (int attempt = 1;
             attempt <= MAX_ATTEMPTS;
             attempt++) {

            System.out.println(
                    "[" + subject + "] Attempt "
                            + attempt + "/" + MAX_ATTEMPTS);

            String response = client.sendPrompt(
                    systemPrompt,
                    prompt.toString(),
                    screenshots
            );

            try {
                JsonNode result =
                        mapper.readTree(response);

                validate(result, input, subject);

                Files.createDirectories(OUTPUT_DIR);

                Files.writeString(
                        outputPath,
                        mapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(result),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW
                );

                System.out.println(
                        "[" + subject + "] Saved: " + outputPath);

                return outputPath;

            } catch (IOException |
                     IllegalArgumentException e) {

                System.err.println(
                        "[" + subject + "] Validation failed: "
                                + e.getMessage());

                if (attempt == MAX_ATTEMPTS) {
                    throw new IOException(
                            "Analysis failed after "
                                    + MAX_ATTEMPTS
                                    + " attempts for " + subject,
                            e);
                }

                prompt.append(
                        "\n\nVALIDATION CORRECTION:\n"
                );
                prompt.append(e.getMessage());
                prompt.append(
                        "\nRegenerate the complete JSON. "
                                + "Include every FP and FN exactly once, "
                                + "using exact input transitions."
                );
            }
        }

        throw new IllegalStateException(
                "Unexpected end of analysis");
    }

    private void validate(
            JsonNode result,
            JsonNode input,
            String subject) {

        if (!subject.equals(
                result.path("subject").asText())) {
            throw new IllegalArgumentException(
                    "Output subject does not match input");
        }

        Map<String, String> expectedErrors =
                new HashMap<>();

        Map<String, String> evaluated =
                new HashMap<>();

        JsonNode inventory =
                input.path("evaluated_transitions");

        if (!inventory.isArray()) {
            throw new IllegalArgumentException(
                    "Invalid input transition inventory");
        }

        for (JsonNode record : inventory) {
            String transition = normalize(
                    record.path("transition").asText());

            String classification =
                    record.path("classification").asText();

            evaluated.put(
                    transition, classification);

            if (classification.equals("FP")
                    || classification.equals("FN")) {

                expectedErrors.put(
                        transition, classification);
            }
        }

        JsonNode analyses =
                result.path("error_analyses");

        if (!analyses.isArray()) {
            throw new IllegalArgumentException(
                    "Missing error_analyses array");
        }

        Set<String> seen = new HashSet<>();

        String[] requiredFields = {
                "pattern_hypothesis",
                "observable_characteristics",
                "matching_criteria",
                "possible_cause",
                "supporting_evidence"
        };

        for (JsonNode analysis : analyses) {
            String transition = normalize(
                    analysis.path("transition").asText());

            String classification =
                    analysis.path("classification").asText();

            if (!seen.add(transition)) {
                throw new IllegalArgumentException(
                        "Duplicate analysis: " + transition);
            }

            if (!classification.equals(
                    expectedErrors.get(transition))) {
                throw new IllegalArgumentException(
                        "Unknown or incorrectly classified error: "
                                + transition);
            }

            for (String field : requiredFields) {
                if (!analysis.path(field).isTextual()
                        || analysis.path(field)
                        .asText().isBlank()) {

                    throw new IllegalArgumentException(
                            "Missing field " + field
                                    + " for " + transition);
                }
            }

            String confidence =
                    analysis.path("confidence").asText();

            if (!Set.of(
                    "low", "medium", "high"
            ).contains(confidence)) {

                throw new IllegalArgumentException(
                        "Invalid confidence for "
                                + transition);
            }

            if (!analysis.path(
                    "requires_review").isBoolean()) {

                throw new IllegalArgumentException(
                        "Missing Boolean requires_review for "
                                + transition);
            }
        }

        if (!seen.equals(expectedErrors.keySet())) {
            Set<String> missing =
                    new HashSet<>(expectedErrors.keySet());

            missing.removeAll(seen);

            throw new IllegalArgumentException(
                    "Missing FP/FN analyses: " + missing);
        }

        JsonNode examples =
                result.path("correct_transition_examples");

        if (!examples.isArray()) {
            throw new IllegalArgumentException(
                    "Missing correct_transition_examples array");
        }

        for (JsonNode example : examples) {
            String transition = normalize(
                    example.path("transition").asText());

            String classification =
                    example.path("classification").asText();

            if (!classification.equals("TP")
                    && !classification.equals("TN")) {
                throw new IllegalArgumentException(
                        "Comparison example is not TP/TN");
            }

            if (!classification.equals(
                    evaluated.get(transition))) {
                throw new IllegalArgumentException(
                        "Unknown comparison transition: "
                                + transition);
            }
        }

        if (result.path(
                "subject_summary").asText().isBlank()) {
            throw new IllegalArgumentException(
                    "Missing subject summary");
        }

        System.out.println(
                "Validated " + seen.size()
                        + " FP/FN analyses for " + subject);
    }

    private static String normalize(String transition) {
        String[] parts =
                transition.trim().split("\\s*->\\s*", -1);

        if (parts.length != 2
                || parts[0].isBlank()
                || parts[1].isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid transition: " + transition);
        }

        return parts[0].trim()
                + " -> "
                + parts[1].trim();
    }

    public static void main(String[] args)
            throws Exception {

        if (args.length != 1) {
            System.err.println(
                    "Usage: LNFErrorPatternDiscoveryAgent "
                            + "<subject>");
            System.exit(1);
        }

        OpenAIClient client =
                new OpenAIClient("gpt-5.4");

        LNFErrorPatternDiscoveryAgent agent =
                new LNFErrorPatternDiscoveryAgent(client);

        agent.analyze(args[0]);
    }
}