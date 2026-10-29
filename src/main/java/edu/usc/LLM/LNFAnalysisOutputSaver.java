package edu.usc.LLM;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Saves the complete Stage 3 LLM analysis for each subject.
 */
public final class LNFAnalysisOutputSaver {

    private LNFAnalysisOutputSaver() { }

    public static Path save(
            Path outputRoot,
            String subject,
            String response) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response);

        if (!root.isObject()) {
            throw new IllegalArgumentException(
                    "LLM analysis response must be a JSON object.");
        }

        JsonNode transitionAnalysis =
                root.get("transition_analysis");

        JsonNode subjectPatterns =
                root.get("subject_patterns");

        if (transitionAnalysis == null ||
                !transitionAnalysis.isArray()) {
            throw new IllegalArgumentException(
                    "Missing transition_analysis array.");
        }

        if (subjectPatterns == null ||
                !subjectPatterns.isArray()) {
            throw new IllegalArgumentException(
                    "Missing subject_patterns array.");
        }

        Path subjectDirectory = outputRoot.resolve(subject);
        Files.createDirectories(subjectDirectory);

        Path outputFile =
                subjectDirectory.resolve(subject + ".json");

        Files.writeString(
                outputFile,
                objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(root),
                StandardCharsets.UTF_8);

        return outputFile;
    }
}