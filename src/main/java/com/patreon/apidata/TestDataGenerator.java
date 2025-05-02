package com.patreon.apidata;

import com.patreon.api.OAuthService;
import com.patreon.api.TokenResponse;
import com.patreon.api.TokenStore;
import com.patreon.api.models.Campaign;
import com.patreon.api.models.Tier;
import com.patreon.utils.MockDataGenerator;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDate;
import java.util.List;

public class TestDataGenerator {
    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.load();
            String clientId = dotenv.get("CLIENT_ID");
            String clientSecret = dotenv.get("CLIENT_SECRET");
            String redirectUri = dotenv.get("REDIRECT_URI");

            OAuthService service = new OAuthService(clientId, clientSecret, redirectUri);
            TokenResponse token = TokenStore.load();

            if (token == null) {
                System.err.println("❌ No saved token found. Authenticate first!");
                return;
            }

            CampaignService campaignService = new CampaignService(token.access_token);
            Campaign campaign = campaignService.getCampaignWithTiers();
            List<Tier> tiers = campaign.tiers;

            // 🔥 Generate 360 days of historical mock data
            LocalDate today = LocalDate.now();
            for (int i = 360; i >= 1; i--) {
                LocalDate date = today.minusDays(i);
                MockDataGenerator.generateFakeDataForDate(date, tiers, "historic_data");
            }

            System.out.println("✅ Mock historical data generated under data/historic_data/");

        } catch (Exception e) {
            System.err.println("❌ Error generating mock data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
