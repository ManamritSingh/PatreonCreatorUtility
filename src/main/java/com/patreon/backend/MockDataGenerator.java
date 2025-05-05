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
            int patrons = random.nextInt(100); // 0 to 99
            double revenue = patrons * (5 + random.nextInt(20)); // between $5–$25 per patron

            snapshots.add(new TierSnapshot(
                    (tier.getTitle() == null || tier.getTitle().isBlank()) ? "Untitled Tier" : tier.getTitle(),
                    patrons,
                    revenue,
                    date,
                    true // isMock
            ));
        }

        repo.saveAll(snapshots);
        System.out.println("✅ Mock data generated for " + label + " on " + date);
    }
}
