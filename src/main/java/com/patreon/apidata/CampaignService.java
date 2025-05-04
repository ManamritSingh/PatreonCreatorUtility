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
        this.httpClient = HttpClient.newHttpClient();
    }

    public Campaign getCampaignWithTiers() throws IOException, InterruptedException {
        String url = "https://www.patreon.com/api/oauth2/v2/campaigns?include=tiers&fields[campaign]=created_at&fields[tier]=title,amount_cents";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        JsonNode dataArray = root.get("data");
        if (dataArray == null || !dataArray.isArray() || dataArray.isEmpty()) {
            System.err.println("❌ No campaign data found!");
            System.err.println("Raw response:");
            System.err.println(response.body());
            return null;
        }

        JsonNode data = dataArray.get(0);  // first campaign
        JsonNode included = root.get("included");

        Campaign campaign = new Campaign();
        campaign.id = data.get("id").asText();
        campaign.creationDate = data.get("attributes").get("created_at").asText();
        campaign.name = "My Campaign";  // Placeholder if needed

        if (included != null && included.isArray()) {
            for (JsonNode node : included) {
                if (node.get("type").asText().equals("tier")) {
                    Tier tier = new Tier();
                    tier.setId(node.get("id").asText());
                    tier.setTitle(node.get("attributes").get("title").asText());
                    tier.setAmountCents(node.get("attributes").get("amount_cents").asInt());
                    campaign.tiers.add(tier);
                }
            }
        }

        return campaign;
    }
}
