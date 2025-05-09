package com.patreon.backend.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleSearchService {

    private final String apiKey;
    private final String cx;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public GoogleSearchService() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("GOOGLE_API_KEY");
        this.cx = dotenv.get("GOOGLE_CSE_ID");
    }

    public String search(String query) {
        HttpUrl url = HttpUrl.parse("https://www.googleapis.com/customsearch/v1").newBuilder()
                .addQueryParameter("key", apiKey)
                .addQueryParameter("cx", cx)
                .addQueryParameter("q", query)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "Google search failed: " + response.code();
            }

            String body = response.body().string();
            JsonNode root = mapper.readTree(body);
            JsonNode firstItem = root.path("items").get(0);
            return firstItem != null
                    ? firstItem.path("snippet").asText("No snippet found.")
                    : "No results from Google.";
        } catch (IOException e) {
            return "Search error: " + e.getMessage();
        }
    }
}