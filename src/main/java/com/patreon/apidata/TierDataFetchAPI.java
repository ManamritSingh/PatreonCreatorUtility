package com.patreon.apidata;

import com.patreon.api.OAuthService;
import com.patreon.api.TokenResponse;
import com.patreon.api.TokenStore;
import com.patreon.api.models.Campaign;
import com.patreon.api.models.Member;
import com.patreon.api.models.Tier;
import com.patreon.utils.CSVExporter;
import com.patreon.utils.CSVExporter.TierSnapshot;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TierDataFetchAPI {

    public static void main(String[] args) {
        try {
            // Load credentials
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

            // Fetch Campaign and Tiers
            CampaignService campaignService = new CampaignService(token.access_token);
            Campaign campaign = campaignService.getCampaignWithTiers();
            List<Tier> tiers = campaign.tiers;

            // Fetch Members
            MemberService memberService = new MemberService(token.access_token);
            List<Member> members = memberService.getMembers(campaign.id);

            // Prepare Tier earnings and counts
            Map<String, Integer> tierPatronCounts = new HashMap<>();
            Map<String, Integer> tierEarnings = new HashMap<>();

            for (Member member : members) {
                if (member.isActive && member.tierId != null) {
                    tierPatronCounts.put(
                            member.tierId,
                            tierPatronCounts.getOrDefault(member.tierId, 0) + 1
                    );
                    tierEarnings.put(
                            member.tierId,
                            tierEarnings.getOrDefault(member.tierId, 0) + member.pledgeAmountCents
                    );
                }
            }

            // Prepare list of TierSnapshots for CSV
            List<TierSnapshot> snapshots = new ArrayList<>();
            for (Tier tier : tiers) {
                int patrons = tierPatronCounts.getOrDefault(tier.id, 0);
                double revenue = tierEarnings.getOrDefault(tier.id, 0) / 100.0;

                snapshots.add(new TierSnapshot(
                        (tier.title == null || tier.title.isBlank()) ? "Untitled Tier" : tier.title,
                        patrons,
                        revenue
                ));
            }

            // Export to CSV
            CSVExporter.export(snapshots, true, LocalDate.now(), null);
            // 🔥 OVERWRITE the CSV when real data is saved


            System.out.println("✅ Real Patreon data exported successfully to data/tier_data.csv!");

        } catch (IOException e) {
            System.err.println("❌ IO Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
