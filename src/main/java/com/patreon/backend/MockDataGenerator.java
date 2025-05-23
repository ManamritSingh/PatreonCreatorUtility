package com.patreon.backend;

import com.patreon.api.models.Tier;
import com.patreon.backend.models.TierSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class MockDataGenerator {

    private static TierSnapshotRepository repo = null;

    @Autowired
    public MockDataGenerator(TierSnapshotRepository repo) {
        this.repo = repo;
    }

    /**
     * Generate mock data for a given list of real tiers for a specific date.
     * @param date Date to store the data against
     * @param tiers List of tiers (real or simulated)
     * @param label Used for logging/debugging
     */
    public void generateFakeDataForDate(LocalDate date, List<Tier> tiers, String label) {
        List<TierSnapshot> snapshots = new ArrayList<>();
        Random random = new Random();

        for (Tier tier : tiers) {
            String tierName = (tier.getTitle() == null || tier.getTitle().isBlank()) ? "Untitled Tier" : tier.getTitle();
            System.out.println("Attempting to create mock data for tier: " + tierName);

            boolean exists = repo.existsByTierNameAndTimestampAndIsMock(tierName, date.toString(),true);
            if (exists) {
                System.out.println("Skipping duplicate for tier: " + tierName + " on " + date);
                continue;
            }

            int patrons = random.nextInt(100);
            double revenue = patrons * (5 + random.nextInt(20));

            snapshots.add(new TierSnapshot(
                    tierName,
                    patrons,
                    revenue,
                    date,
                    true
            ));
        }


        repo.saveAll(snapshots);
        System.out.println("Mock data generated for " + label + " on " + date);
    }

    public void generateOneYearOfFakeData(List<Tier> tiers) {
        LocalDate today = LocalDate.now();
        for (int i = 360; i >= 1; i--) {
            LocalDate date = today.minusDays(i);
            boolean skip = true;

            for (Tier tier : tiers) {
                String tierName = (tier.getTitle() == null || tier.getTitle().isBlank()) ? "Untitled Tier" : tier.getTitle();
                if (!repo.existsByTierNameAndTimestampAndIsMock(tierName, date.toString(), true)) {
                    skip = false;
                    break;
                }
            }

            if (!skip) {
                generateFakeDataForDate(date, tiers, "Yearly Seeder");
            }
        }
        System.out.println("360 days of mock data seeded to database!");
    }


}
