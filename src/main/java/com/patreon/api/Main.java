package com.patreon.api;

import com.patreon.api.models.Campaign;
import com.patreon.api.models.Member;
import com.patreon.api.models.Tier;
import io.github.cdimascio.dotenv.Dotenv;
import com.patreon.apidata.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) throws Exception {
    	Dotenv dotenv = Dotenv.load();

        String clientId = dotenv.get("CLIENT_ID");
        String clientSecret = dotenv.get("CLIENT_SECRET");
        String redirectUri = dotenv.get("REDIRECT_URI");

        OAuthService service = new OAuthService(clientId, clientSecret, redirectUri);
        TokenResponse token = null;

        if (TokenStore.exists()) {
            token = TokenStore.load();
            System.out.println("🔄 Loaded token from file.");
            if (token.isExpired()) {
                System.out.println("🔁 Token expired. Refreshing...");
                token = service.refreshToken(token.refresh_token);
                TokenStore.save(token);
                System.out.println("✅ Refreshed token saved.");
            } else {
                System.out.println("✅ Token is still valid.");
            }
        }

        if (token == null) {
            // Start redirect server
            OAuthRedirectServer.start(8080);

            // Print URL to log in
            String authUrl = service.getAuthorizationUrl(
                    "identity", "identity[email]", "campaigns", "campaigns.members"
            );
            System.out.println("🔗 Go to this URL to authorize:");
            System.out.println(authUrl);

            // Wait for user to finish login
            while (OAuthRedirectServer.getCode() == null) {
                Thread.sleep(1000);
            }

            String code = OAuthRedirectServer.getCode();
            System.out.println("✅ Received code: " + code);

            // Exchange code for token
            token = service.exchangeCodeForToken(code);
            TokenStore.save(token);
            System.out.println("💾 Token saved to file.");
        }
        //basic handshake
        System.out.println("✅ Access Token Ready: " + token.access_token);

        PatreonClient client = new PatreonClient(token.access_token);
        String userJson = client.getUserIdentity();
        System.out.println("👤 User Info:");
        System.out.println(userJson);

        // Test campaign data
        CampaignService campaignService = new CampaignService(token.access_token);
        Campaign campaign = campaignService.getCampaignWithTiers();

        System.out.println("📢 Campaign: " + campaign.name);
        System.out.println("🎯 Tiers:");
        for (Tier tier : campaign.tiers) {
            System.out.printf("- %s (%d cents)\n", tier.getTitle(), tier.getAmountCents());
        }

        /* Test code for member-tier data */

        MemberService memberService = new MemberService(token.access_token);
        List<Member> members = memberService.getMembers(campaign.id);

        // Compute earnings per tier
        Map<String, Integer> earningsByTier = new HashMap<>();
        for (Member member : members) {
            if (member.getTierId() != null || member.isFollower())
                continue;
            earningsByTier.put(member.getTierId(),
                    earningsByTier.getOrDefault(member.getTierId(), 0) + member.getPledgeAmountCents()
            );
        }

        // Display earnings by tier name
        System.out.println("💰 Earnings by Tier:");
        for (Tier tier : campaign.tiers) {
            int total = earningsByTier.getOrDefault(tier.getId(), 0);
            System.out.printf("- %s: $%.2f\n", tier.getTitle(), total / 100.0);
        }

        // Group members by tier
        Map<String, Integer> tierPatronCounts = new HashMap<>();
        Map<String, Integer> tierEarnings = new HashMap<>();

        for (Member member : members) {
            if (member.isActive() && member.getTierId() != null) {
                // Patron count
                tierPatronCounts.put(
                        member.getTierId(),
                        tierPatronCounts.getOrDefault(member.getTierId(), 0) + 1
                );

                // Revenue
                tierEarnings.put(
                        member.getTierId(),
                        tierEarnings.getOrDefault(member.getTierId(), 0) + member.getPledgeAmountCents()
                );
            }
        }

        System.out.println("\n📊 Tier-wise Patron Count and Revenue:");
        for (Tier tier : campaign.tiers) {
            int count = tierPatronCounts.getOrDefault(tier.getId(), 0);
            int earningsCents = tierEarnings.getOrDefault(tier.getId(), 0);
            double earningsDollars = earningsCents / 100.0;

            System.out.printf("- %s: %d patrons, $%.2f total earnings\n", tier.getTitle(), count, earningsDollars);
        }

    }
}
