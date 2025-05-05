package com.patreon.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class TokenStore {
    private static final String FILE_PATH = "patreon_tokens.json";

    public static void save(TokenResponse token) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(FILE_PATH), token);
    }

    public static TokenResponse load() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) return null;

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, TokenResponse.class);
    }

    public static boolean exists() {
        return new File(FILE_PATH).exists();
    }
}
