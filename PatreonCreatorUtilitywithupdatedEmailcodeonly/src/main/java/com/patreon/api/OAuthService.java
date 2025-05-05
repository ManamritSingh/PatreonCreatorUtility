package com.patreon.api;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class OAuthService {

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final HttpClient httpClient;

    public OAuthService(String clientId, String clientSecret, String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.httpClient = HttpClient.newHttpClient();
    }

    // STEP 1: Generate URL to redirect user to login
    public String getAuthorizationUrl(String... scopes) {
        String scopeString = String.join(" ", scopes);
        return String.format(
                "https://www.patreon.com/oauth2/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=%s",
                urlEncode(clientId), urlEncode(redirectUri), urlEncode(scopeString)
        );
    }

    // STEP 2: Exchange the code for access + refresh tokens
    public TokenResponse exchangeCodeForToken(String code) throws Exception {
        String formData = String.format(
                "code=%s&grant_type=authorization_code&client_id=%s&client_secret=%s&redirect_uri=%s",
                urlEncode(code), urlEncode(clientId), urlEncode(clientSecret), urlEncode(redirectUri)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.patreon.com/api/oauth2/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return TokenResponse.fromJson(response.body());
    }

    // STEP 3: Refresh tokens (optional)
    public TokenResponse refreshToken(String refreshToken) throws Exception {
        String formData = String.format(
                "grant_type=refresh_token&refresh_token=%s&client_id=%s&client_secret=%s",
                urlEncode(refreshToken), urlEncode(clientId), urlEncode(clientSecret)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.patreon.com/api/oauth2/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return TokenResponse.fromJson(response.body());
    }

    private String urlEncode(String val) {
        return URLEncoder.encode(val, StandardCharsets.UTF_8);
    }
}
