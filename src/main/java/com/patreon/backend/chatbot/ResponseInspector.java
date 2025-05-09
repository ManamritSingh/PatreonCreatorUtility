package com.patreon.backend.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;

public class ResponseInspector {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_API_KEY");

        String prompt = "What's the weather today?";

        String requestJson = """
        {
          "model": "gpt-4",
          "messages": [
            { "role": "user", "content": "%s" }
          ]
        }
        """.formatted(prompt.replace("\"", "\\\""));

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(
                requestJson, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("❌ API error: " + response.code() + " " + response.message());
                return;
            }

            String responseBody = response.body().string();
            System.out.println("=== RAW JSON RESPONSE ===\n" + responseBody + "\n");

            JsonNode root = mapper.readTree(responseBody);
            JsonNode message = root.path("choices").get(0).path("message").path("content");
            System.out.println("=== EXTRACTED MESSAGE ===\n" + message.asText());

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
