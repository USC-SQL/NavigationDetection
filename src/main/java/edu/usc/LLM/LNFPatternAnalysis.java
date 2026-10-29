package edu.usc.LLM;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Independently analyzes TP, FP, and FN transitions using
 * navigation data, ground truth, and annotated screenshots.
 */
public final class LNFPatternAnalysis {

    private static final Path PROMPT_PATH =
            Path.of("src/main/resources/LLM/LNFAnalysis_prompt.txt");

    private final OpenAIClient openAIClient;

    public LNFPatternAnalysis(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public String analyze(
            String subject,
            Path analysisInputFile,
            List<Path> screenshots)
            throws IOException, InterruptedException {

        String promptTemplate = Files.readString(
                PROMPT_PATH,
                StandardCharsets.UTF_8);

        String analysisInput = Files.readString(
                analysisInputFile,
                StandardCharsets.UTF_8);

        StringBuilder prompt = new StringBuilder();

        prompt.append(promptTemplate);
        prompt.append("\n\n");

        prompt.append("Subject: ")
                .append(subject)
                .append("\n\n");

        prompt.append("Analysis input JSON:\n");
        prompt.append(analysisInput);

        String systemPrompt =
                "You are an independent accessibility research analyst. "
                        + "Analyze LNF detection accuracy under WCAG 2.4.3. "
                        + "Use only the supplied navigation data, "
                        + "evaluation classifications, and screenshots. "
                        + "Provide evidence-supported explanations. "
                        + "Return only valid JSON.";

        String response = openAIClient.sendPrompt(
                systemPrompt,
                prompt.toString(),
                screenshots);

        LNFAnalysisValidator.validate(
                response,
                analysisInput);

        return response;
    }
}