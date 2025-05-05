package com.patreon.utils;

import com.patreon.api.models.Tier;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.patreon.utils.CSVExporter.TierSnapshot;

public class MockDataGenerator {

    public static void generateFakeDataForDate(LocalDate date, List<Tier> tiers, String subdirectory) throws IOException {
        List<TierSnapshot> fakeData = new ArrayList<>();
        Random random = new Random();

        for (Tier tier : tiers) {
            int patrons;
            if (tier.getAmountCents() == 0) {
                patrons = random.nextInt(51) + 50;
            } else if (tier.getAmountCents() <= 200) {
                patrons = random.nextInt(31) + 20;
            } else if (tier.getAmountCents() <= 500) {
                patrons = random.nextInt(21) + 10;
            } else {
                patrons = random.nextInt(11) + 5;
            }

            double revenue = patrons * (tier.getAmountCents() / 100.0);

            fakeData.add(new TierSnapshot(
                    (tier.getTitle() == null || tier.getTitle().isBlank()) ? "Untitled Tier" : tier.getTitle(),
                    patrons,
                    revenue
            ));
        }

        CSVExporter.export(fakeData, false, date, subdirectory);
    }
}
