package com.patreon.apidata;

import com.patreon.api.OAuthService;
import com.patreon.api.TokenResponse;
import com.patreon.api.TokenStore;
import com.patreon.api.models.Campaign;
import com.patreon.api.models.Member;
import com.patreon.api.models.Tier;
import com.patreon.backend.models.TierSnapshot;
import com.patreon.backend.TierSnapshotRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class TierDataFetchAPI implements CommandLineRunner {

    @Autowired
    private TierSnapshotRepository snapshotRepository;

    @Override
    public void run(String... args) throws Exception {
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

            MemberService memberService = new MemberService(token.access_token);
            List<Member> members = memberService.getMembers(campaign.id);

            Map<String, Integer> tierPatronCounts = new HashMap<>();
            Map<String, Integer> tierEarnings = new HashMap<>();

            for (Member member : members) {
                if (member.isActive() && member.getTierId() != null) {
                    tierPatronCounts.merge(member.getTierId(), 1, Integer::sum);
                    tierEarnings.merge(member.getTierId(), member.getPledgeAmountCents(), Integer::sum);
                }
            }

            List<TierSnapshot> snapshots = new ArrayList<>();
            LocalDate now = LocalDate.now();

            for (Tier tier : tiers) {
                int patrons = tierPatronCounts.getOrDefault(tier.getId(), 0);
                double revenue = tierEarnings.getOrDefault(tier.getId(), 0) / 100.0;

                snapshots.add(new TierSnapshot(
                        tier.getTitle() == null || tier.getTitle().isBlank() ? "Untitled Tier" : tier.getTitle(),
                        patrons,
                        revenue,
                        now,
                        false // isMock = false for real data
                ));
            }

            snapshotRepository.saveAll(snapshots);
            System.out.println("✅ Real data saved to database successfully!");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
