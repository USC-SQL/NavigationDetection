package edu.usc.LLM;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Uses annotated screenshots and KFG element descriptors to infer
 * the meaningful navigation order of a subject webpage.
 */
public final class NavigationOrderInference {

    private static final Path PROMPT_PATH =
            Path.of("src/main/resources/LLM/NavOrder_prompt.txt");

    private final OpenAIClient openAIClient;

    public NavigationOrderInference(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public String infer(
            String subject,
            List<String> elements,
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

        prompt.append("KFG element descriptors:\n");

        for (int i = 0; i < elements.size(); i++) {
            prompt.append(i + 1)
                    .append(": ")
                    .append(elements.get(i))
                    .append("\n");
        }

        String systemPrompt =
                "You reconstruct meaningful keyboard navigation order "
                        + "from annotated webpage screenshots and KFG element data. "
                        + "Return only valid JSON.";

        String response = openAIClient.sendPrompt(
                systemPrompt,
                prompt.toString(),
                screenshots);

        NavigationOrderValidator.validate(response, elements);

        return response;
    }
}