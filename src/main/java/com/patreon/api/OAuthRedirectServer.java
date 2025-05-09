package com.patreon.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class OAuthRedirectServer {

    private static String code;

    public static void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/callback", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.contains("code=")) {
                    code = query.split("code=")[1].split("&")[0];
                    String response = "Authorization successful! You can close this tab.";
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    server.stop(1);  // Stop the server after handling this
                }
            }
        });

        server.setExecutor(null); // Use default thread
        server.start();
    }

    public static String getCode() {
        return code;
    }
}
