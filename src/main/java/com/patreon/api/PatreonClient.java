package com.patreon.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PatreonClient {
    private final String accessToken;
    private final HttpClient httpClient;

    public PatreonClient(String accessToken) {
        this.accessToken = accessToken;
        this.httpClient = HttpClient.newHttpClient();
    }

    public String getUserIdentity() throws IOException, InterruptedException {
        String url = "https://www.patreon.com/api/oauth2/v2/identity" +
                "?include=memberships&fields[user]=full_name,email,about,image_url";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .header("User-Agent", "JavaPatreonClient/1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            System.err.println("Request failed: " + response.statusCode());
            System.err.println("Response: " + response.body());
            return null;
        }
    }
}
