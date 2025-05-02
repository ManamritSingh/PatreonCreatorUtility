package com.patreon.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {
    public String access_token;
    public String refresh_token;
    public int expires_in;
    public String token_type;
    public String scope;

    public long timestamp = System.currentTimeMillis(); // when the token was saved

    public static TokenResponse fromJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, TokenResponse.class);
    }

    public boolean isExpired() {
        long now = System.currentTimeMillis();
        return (now - timestamp) > (expires_in * 1000L - 60_000); // refresh 1 min early
    }
}
