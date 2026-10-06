package edu.usc.LLM;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Uses the previously inferred navigation order and annotated screenshots
 * to identify suspected Linear Navigation Failures under WCAG 2.4.3.
 */
public final class LNFDetection {

    private static final Path PROMPT_PATH =
            Path.of("src/main/resources/LLM/LNFDetection_prompt.txt");

    private final OpenAIClient openAIClient;

    public LNFDetection(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public String detect(
            String subject,
            String navigationOrderJson,
            List<Path> screenshots)
            throws IOException, InterruptedException {

        String promptTemplate = Files.readString(
                PROMPT_PATH,
                StandardCharsets.UTF_8);

        StringBuilder prompt = new StringBuilder();

        prompt.append("Subject name: ")
                .append(subject)
                .append("\n\n");

        prompt.append(promptTemplate);
        prompt.append("\n\n");

        prompt.append("Navigation order JSON for this subject:\n");
        prompt.append(navigationOrderJson);
        prompt.append("\n");

        String systemPrompt =
                "You evaluate keyboard focus order for Linear Navigation "
                        + "Failures under WCAG Success Criterion 2.4.3. "
                        + "Use the provided navigation data and annotated screenshots. "
                        + "Return only valid JSON.";

        String response = openAIClient.sendPrompt(
                systemPrompt,
                prompt.toString(),
                screenshots);

        LNFDetectionValidator.validate(
                response,
                navigationOrderJson);

        return response;
    }
}