package edu.usc.LLM;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class LNFOutputSaver {

    private LNFOutputSaver() { }

    public static Path save(
            Path outputRoot,
            String subject,
            String response) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response);
        JsonNode lnfAnalysis = root.get("lnf_analysis");

        if (lnfAnalysis == null || !lnfAnalysis.isArray()) {
            throw new IllegalArgumentException(
                    "Response does not contain an lnf_analysis array.");
        }

        List<String> transitions = new ArrayList<>();

        for (JsonNode entry : lnfAnalysis) {
            String transition = entry.path("transition").asText("");

            if (!transition.isBlank()) {
                transitions.add(transition);
            }
        }

        Path subjectDirectory = outputRoot.resolve(subject);
        Files.createDirectories(subjectDirectory);

        Path outputFile =
                subjectDirectory.resolve(subject + ".txt");

        Files.write(outputFile, transitions);

        return outputFile;
    }
}