package com.patreon.apidata;

import com.patreon.api.OAuthService;
import com.patreon.api.TokenResponse;
import com.patreon.api.TokenStore;
import com.patreon.api.models.Campaign;
import com.patreon.api.models.Tier;
import com.patreon.backend.MockDataGenerator;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TestDataGenerator implements CommandLineRunner {

    private final MockDataGenerator mockDataGenerator;

    public TestDataGenerator(MockDataGenerator mockDataGenerator) {
        this.mockDataGenerator = mockDataGenerator;
    }

    @Override
    public void run(String... args) {
        try {
            Dotenv dotenv = Dotenv.load();
            String clientId = dotenv.get("CLIENT_ID");
            String clientSecret = dotenv.get("CLIENT_SECRET");
            String redirectUri = dotenv.get("REDIRECT_URI");

            OAuthService service = new OAuthService(clientId, clientSecret, redirectUri);
            TokenResponse token = TokenStore.load();

            if (token == null) {
                System.err.println("No saved token found. Authenticate first!");
                return;
            }

            CampaignService campaignService = new CampaignService(token.access_token);
            Campaign campaign = campaignService.getCampaignWithTiers();
            List<Tier> tiers = campaign.tiers;

            LocalDate today = LocalDate.now();

            for (int i = 360; i >= 1; i--) {
                LocalDate date = today.minusDays(i);
                mockDataGenerator.generateFakeDataForDate(date, tiers, "TestDataGenerator");
            }

            System.out.println("360 days of mock data seeded to database!");

        } catch (Exception e) {
            System.err.println("Error generating mock data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
