package com.patreon.backend.execution;

import com.patreon.apidata.CampaignService;
import com.patreon.api.OAuthService;
import com.patreon.api.TokenResponse;
import com.patreon.api.TokenStore;
import com.patreon.api.models.Campaign;
import com.patreon.backend.MockDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.cdimascio.dotenv.Dotenv;

@RestController
@RequestMapping("/api/data")
public class DataSeederController {

    private final DataSeeder dataSeeder;
    private final MockDataGenerator mockDataGenerator;

    @Autowired
    public DataSeederController(DataSeeder dataSeeder, MockDataGenerator mockDataGenerator) {
        this.dataSeeder = dataSeeder;
        this.mockDataGenerator = mockDataGenerator;
    }

    @PostMapping("/generate")
    public String generateData(@RequestParam boolean mock) {
        dataSeeder.seedData(mock);
        return mock ? "✅ Fake data generated!" : "✅ Real data generated!";
    }

    @PostMapping("/generate/yearly-fake")
    public ResponseEntity<String> generateYearlyFakeData() {
        try {
            Dotenv dotenv = Dotenv.load();
            OAuthService service = new OAuthService(
                    dotenv.get("CLIENT_ID"),
                    dotenv.get("CLIENT_SECRET"),
                    dotenv.get("REDIRECT_URI")
            );

            TokenResponse token = TokenStore.load();
            if (token == null) return ResponseEntity.status(401).body("No token found");

            CampaignService campaignService = new CampaignService(token.access_token);
            Campaign campaign = campaignService.getCampaignWithTiers();
            mockDataGenerator.generateOneYearOfFakeData(campaign.tiers);

            return ResponseEntity.ok("✅ 360 days of mock data generated.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Failed: " + e.getMessage());
        }
    }
}
