package com.patreon.backend.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final GoogleSearchService googleSearchService;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey;
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public ChatService(ConversationRepository repository, GoogleSearchService googleSearchService) {
        this.conversationRepository = repository;
        this.googleSearchService = googleSearchService;
        this.apiKey = Dotenv.load().get("OPENAI_API_KEY");
    }

    private String extractKeywords(String input) {
        String lower = input.toLowerCase();

        // Remove common filler patterns (natural language prefaces)
        lower = lower.replaceAll("\\b(please|can you|could you|would you|will you|may you|i want to know|tell me|show me|give me|let me know|what is|what's|whats|who is|top|latest|news about|update on|current|current events|today|now|happening in|what happened in|what do you know about)\\b", "");

        // Remove stopwords and filler
        lower = lower.replaceAll("\\b(the|is|a|an|in|on|at|of|to|and|or|for|from|by)\\b", "");

        // Remove punctuation and trim whitespace
        lower = lower.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", " ").trim();

        // Fallback: if result is too short, return original input
        return (lower.length() < 3) ? input : lower;
    }


    public String getResponse(String sessionId, String userInput) {
        // Save user message
        ChatMessage userMsg = new ChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setRole("user");
        userMsg.setMessage(userInput);
        conversationRepository.save(userMsg);

        // Build context from history
        List<ChatMessage> history = conversationRepository.findBySessionIdOrderByTimestampAsc(sessionId);
        System.out.println("📜 Loaded " + history.size() + " messages for session: " + sessionId);
        System.out.println("🧠 Fetched " + history.size() + " messages for sessionId: " + sessionId);
        for (int i = 0; i < history.size(); i++) {
            ChatMessage m = history.get(i);
            if (m == null) {
                System.out.println("⚠️ history[" + i + "] is null");
            } else {
                System.out.println("📄 [" + m.getRole() + "] " + m.getMessage());
            }
        }


        List<String> messageList = new ArrayList<>();

        messageList.add("""
        { "role": "system", "content": "You are a helpful AI assistant integrated into a Java-based desktop application. Your responses are used in a chatbot interface. When the user asks about real-world topics, especially recent events, your developer has configured a Google Search layer that provides search result snippets before you are called. You can assume these snippets are accurate and timely, and you should incorporate them into your answers naturally. If such snippets appear in the conversation, treat them as reliable context and answer accordingly.Also make sure that the one you are talking to thinks that you know it all by yourself, You are very smart according to the user" }
        """);

        for (ChatMessage m : history) {
            if (m == null) {
                System.out.println("⚠️ Skipping null message.");
                continue;
            }
            if (m.getRole() == null || m.getMessage() == null) {
                System.out.println("⚠️ Skipping incomplete message: " + m);
                continue;
            }

            messageList.add(String.format("""
                { "role": "%s", "content": "%s" }
                """, m.getRole(), escapeJson(m.getMessage())));
        }

        // Optionally use search
        if (shouldUseSearch(userInput)) {
            String keywordQuery = extractKeywords(userInput);
            System.out.println("🔍 Extracted query: " + keywordQuery);
            String searchSnippet = googleSearchService.search(keywordQuery);

            System.out.println("🔎 Search result added to context: " + searchSnippet);
            messageList.add("""
            { "role": "user", "content": "I found this recently online:\\n%s\\nPlease summarize or explain." }
            """.formatted(escapeJson(searchSnippet)));

        }

        String requestJson = """
        {
          "model": "gpt-4",
          "messages": [%s]
        }
        """.formatted(String.join(",", messageList));

        System.out.println("🧾 Final JSON sent to OpenAI:\n" + requestJson);

        // Call OpenAI
        try {
            RequestBody body = RequestBody.create(requestJson, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(OPENAI_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return "❌ OpenAI error: " + response.code() + " " + response.message();

                }

                String responseBody = response.body().string();
                JsonNode root = mapper.readTree(responseBody);
                String reply = root.path("choices").get(0).path("message").path("content").asText();

                // Save bot message
                ChatMessage botMsg = new ChatMessage();
                botMsg.setSessionId(sessionId);
                botMsg.setRole("assistant");
                botMsg.setMessage(reply);
                conversationRepository.save(botMsg);

                return reply;
            }

        } catch (IOException e) {
            return "❌ Request failed: " + e.getMessage();
        }
    }

    private boolean shouldUseSearch(String input) {
        String lower = input.toLowerCase();
        return lower.contains("today") || lower.contains("latest") || lower.contains("news");
    }

    private String escapeJson(String s) {
        return s.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
