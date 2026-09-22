package edu.usc.LLM;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Reusable client for sending text prompts and images to the OpenAI API.
 */
public final class OpenAIClient {

    private static final String API_URL =
            "https://api.openai.com/v1/chat/completions";

    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenAIClient(String model) throws IOException {
        this.apiKey = loadApiKey();
        this.model = model;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Sends a prompt and zero or more images to the model.
     *
     * @return the text contained in the model's response
     */
    public String sendPrompt(
            String systemPrompt,
            String userPrompt,
            List<Path> images)
            throws IOException, InterruptedException {

        List<Object> content = new ArrayList<>();

        for (Path image : images) {
            String base64 = Base64.getEncoder()
                    .encodeToString(Files.readAllBytes(image));

            String mediaType = getMediaType(image);

            content.add(objectMapper.createObjectNode()
                    .put("type", "image_url")
                    .set("image_url",
                            objectMapper.createObjectNode()
                                    .put("url",
                                            "data:" + mediaType
                                                    + ";base64," + base64)));
        }

        content.add(objectMapper.createObjectNode()
                .put("type", "text")
                .put("text", userPrompt));

        var systemMessage = objectMapper.createObjectNode()
                .put("role", "system")
                .put("content", systemPrompt);

        var userMessage = objectMapper.createObjectNode()
                .put("role", "user")
                .set("content", objectMapper.valueToTree(content));

        var requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.set(
                "messages",
                objectMapper.valueToTree(
                        List.of(systemMessage, userMessage)));

        var responseFormat = objectMapper.createObjectNode()
                .put("type", "json_object");

        requestBody.set("response_format", responseFormat);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        objectMapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException(
                    "OpenAI API request failed with status "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());

        return root.path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText();
    }

    private static String loadApiKey() throws IOException {
        String environmentKey = System.getenv("OPENAI_API_KEY");

        if (environmentKey != null && !environmentKey.isBlank()) {
            return environmentKey.trim();
        }

        Path envFile = Path.of(".env");

        if (!Files.exists(envFile)) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY was not found in the environment "
                            + "and .env does not exist.");
        }

        for (String line : Files.readAllLines(
                envFile, StandardCharsets.UTF_8)) {

            String trimmed = line.trim();

            if (trimmed.startsWith("OPENAI_API_KEY=")) {
                String key = trimmed.substring(
                        "OPENAI_API_KEY=".length()).trim();

                if (!key.isBlank()) {
                    return key;
                }
            }
        }

        throw new IllegalStateException(
                "OPENAI_API_KEY was not found in .env.");
    }

    private static String getMediaType(Path image) {
        String name = image.getFileName()
                .toString()
                .toLowerCase();

        if (name.endsWith(".png")) {
            return "image/png";
        }

        if (name.endsWith(".webp")) {
            return "image/webp";
        }

        return "image/jpeg";
    }
}