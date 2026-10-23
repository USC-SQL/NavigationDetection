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

    private static final int MAX_ATTEMPTS = 3;

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

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            System.out.println(
                    "LNF detection attempt "
                            + attempt + "/" + MAX_ATTEMPTS);

            String response = openAIClient.sendPrompt(
                    systemPrompt,
                    prompt.toString(),
                    screenshots);

            try {
                LNFDetectionValidator.validate(
                        response,
                        navigationOrderJson);

                return response;

            } catch (IOException | IllegalArgumentException e) {

                System.out.println(
                        "LNF validation failed: " + e.getMessage());

                if (attempt == MAX_ATTEMPTS) {
                    throw new IOException(
                            "LNF detection failed validation after "
                                    + MAX_ATTEMPTS
                                    + " attempts for subject: "
                                    + subject,
                            e);
                }

                prompt.append("\n\n");
                prompt.append("IMPORTANT: Your previous response failed validation.\n");
                prompt.append("Validation error: ")
                        .append(e.getMessage())
                        .append("\n");

                prompt.append(
                        "Please regenerate the complete lnf_analysis JSON. "
                                + "Each reported transition must connect "
                                + "two elements with consecutive implemented "
                                + "keyboard labels, N -> N+1. "
                                + "The label field determines implemented "
                                + "keyboard navigation order, NOT the position field. "
                                + "Use exact XPaths from the supplied JSON. "
                                + "Do not invent transitions. "
                                + "If no valid LNFs are identified, return "
                                + "{\"lnf_analysis\":[]}.");
            }
        }

        throw new IllegalStateException(
                "Unexpected end of LNF detection.");
    }
}