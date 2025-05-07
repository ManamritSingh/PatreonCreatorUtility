package com.patreon.backend.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final OpenAIClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public ChatService() {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_API_KEY");

        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    public String getResponse(String userInput) {
        try {
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .input(userInput)
                    .model(ChatModel.GPT_4_1)
                    .build();

            Object raw = client.responses().create(params);

            String jsonString = mapper.writeValueAsString(raw);
            JsonNode root = mapper.readTree(jsonString);

            JsonNode outputArray = root.path("output");
            if (!outputArray.isArray() || outputArray.isEmpty()) {
                return "⚠️ No output in response.";
            }

            JsonNode contentArray = outputArray.get(0).path("content");
            if (!contentArray.isArray() || contentArray.isEmpty()) {
                return "⚠️ No content in output.";
            }

            JsonNode textNode = contentArray.get(0).path("text");

            if (textNode.isMissingNode() || textNode.isNull()) {
                return "⚠️ No text found in content.";
            }

            return textNode.asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error parsing AI response: " + e.getMessage();
        }
    }

}

