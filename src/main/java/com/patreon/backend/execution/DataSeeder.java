package com.patreon.backend.execution;

import com.patreon.api.OAuthService;
import com.patreon.api.TokenResponse;
import com.patreon.api.TokenStore;
import com.patreon.api.models.Campaign;
import com.patreon.api.models.Member;
import com.patreon.api.models.Tier;
import com.patreon.backend.TierSnapshotRepository;
import com.patreon.backend.models.TierSnapshot;
import com.patreon.backend.MockDataGenerator;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class DataSeeder {


    private final TierSnapshotRepository snapshotRepository;
    private final MockDataGenerator mockDataGenerator;

    public DataSeeder(TierSnapshotRepository snapshotRepository, MockDataGenerator mockDataGenerator) {
        this.snapshotRepository = snapshotRepository;
        this.mockDataGenerator = mockDataGenerator;
    }

    public void seedData(boolean useMock) {
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

            com.patreon.apidata.CampaignService campaignService = new com.patreon.apidata.CampaignService(token.access_token);
            Campaign campaign = campaignService.getCampaignWithTiers();
            List<Tier> tiers = campaign.tiers;
            LocalDate today = LocalDate.now();

            if (useMock) {
                // 🔁 Generate mock data
                mockDataGenerator.generateFakeDataForDate(today, tiers, "startup-seed");
            } else {
                // 🧠 Use MemberService to compute real data
                com.patreon.apidata.MemberService memberService = new com.patreon.apidata.MemberService(token.access_token);
                List<Member> members = memberService.getMembers(campaign.id);

                // Group member count by tier
                Map<String, Integer> patronCounts = new HashMap<>();
                for (Member m : members) {
                    if (m.isActive() && m.getTierId() != null) {
                        patronCounts.put(
                                m.getTierId(),
                                patronCounts.getOrDefault(m.getTierId(), 0) + 1
                        );
                    }
                }

                // If any tier already has data for today, abort the entire operation
                boolean alreadyExists = tiers.stream().anyMatch(tier ->
                        snapshotRepository.existsByTierNameAndTimestampAndIsMock(
                                tier.getTitle() == null ? "Untitled Tier" : tier.getTitle(),
                                today.toString(),
                                false
                        )
                );

                if (alreadyExists) {
                    System.out.println("⚠Tier snapshot data already exists for today. Skipping real data seeding.");
                    return;
                }

                // ✅ Generate and store fresh snapshot data
                List<TierSnapshot> snapshots = new ArrayList<>();
                for (Tier tier : tiers) {
                    int patrons = patronCounts.getOrDefault(tier.getId(), 0);
                    double revenue = patrons * (tier.getAmountCents() / 100.0);

                    snapshots.add(new TierSnapshot(
                            tier.getTitle() == null ? "Untitled Tier" : tier.getTitle(),
                            patrons,
                            revenue,
                            today,
                            false // isMock = false
                    ));
                }

                snapshotRepository.saveAll(snapshots);
                System.out.println("Saved real members to database!");
            }


        } catch (Exception e) {
            System.err.println("Data seeding failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
