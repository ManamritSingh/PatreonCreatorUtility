package com.patreon.apidata;
// to fetch all campaign and tiers

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patreon.api.models.Campaign;
import com.patreon.api.models.Tier;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CampaignService {

    private final HttpClient httpClient;
    private final String accessToken;

    public CampaignService(String accessToken) {
        this.accessToken = accessToken;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(5))  // connection timeout
                .build();
    }


    public Campaign getCampaignWithTiers() {
        String url = "https://www.patreon.com/api/oauth2/v2/campaigns" +
                "?include=tiers&fields[campaign]=created_at&fields[tier]=title,amount_cents";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(java.time.Duration.ofSeconds(15)) // total request timeout
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 500) {
                System.err.println("⚠️ Patreon API returned server error: " + response.statusCode());
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());

            JsonNode dataArray = root.get("data");
            if (dataArray == null || !dataArray.isArray() || dataArray.isEmpty()) {
                System.err.println("❌ No campaign data found!");
                System.err.println("Raw response:\n" + response.body());
                return null;
            }

            JsonNode data = dataArray.get(0);
            JsonNode included = root.get("included");

            Campaign campaign = new Campaign();
            campaign.id = data.get("id").asText();
            campaign.creationDate = data.get("attributes").get("created_at").asText();
            campaign.name = "My Campaign";  // or replace with actual name

            if (included != null && included.isArray()) {
                for (JsonNode node : included) {
                    if ("tier".equals(node.get("type").asText())) {
                        Tier tier = new Tier();
                        tier.setId(node.get("id").asText());
                        tier.setTitle(node.get("attributes").get("title").asText());
                        tier.setAmountCents(node.get("attributes").get("amount_cents").asInt());
                        campaign.tiers.add(tier);
                    }
                }
            }

            return campaign;

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Failed to fetch campaign from Patreon API: " + e.getMessage());
            return null;
        }
    }

}
