package com.patreon.apidata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patreon.api.models.Member;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MemberService {
    private final HttpClient httpClient;
    private final String accessToken;

    public MemberService(String accessToken) {
        this.accessToken = accessToken;
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<Member> getMembers(String campaignId) throws IOException, InterruptedException {
        List<Member> members = new ArrayList<>();
        String url = "https://www.patreon.com/api/oauth2/v2/campaigns/" + campaignId +
                "/members?include=user&fields[member]=full_name,is_follower&fields[user]=full_name,email";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        JsonNode dataArray = root.get("data");
        if (dataArray == null || !dataArray.isArray()) {
            System.err.println("❌ No members found!");
            System.err.println("Raw response:");
            System.err.println(response.body());
            return members;
        }

        for (JsonNode node : dataArray) {
            Member m = new Member();
            m.setId(node.get("id").asText());

            JsonNode pledgeNode = node.get("attributes").get("pledge_amount_cents");
            m.setPledgeAmountCents((pledgeNode != null && !pledgeNode.isNull())
                    ? pledgeNode.asInt()
                    : 0);


            m.setActive(!node.get("attributes").get("is_follower").asBoolean());

            // Extract first tier ID (if any)
            JsonNode relationships = node.get("relationships");
            if (relationships != null) {
                JsonNode entitled = relationships.get("currently_entitled_tiers");
                if (entitled != null) {
                    JsonNode entitledTiers = entitled.get("data");
                    if (entitledTiers != null && entitledTiers.isArray() && entitledTiers.size() > 0) {
                        m.setTierId(entitledTiers.get(0).get("id").asText());
                    }
                }
            }


            members.add(m);
        }

        return members;
    }
}
