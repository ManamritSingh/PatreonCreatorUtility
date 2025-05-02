package com.patreon.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class CSVExporter {

    public static void export(List<TierSnapshot> snapshots, boolean overwrite, LocalDate date, String subdirectory) throws IOException {
        String folderPath = "data/" + (subdirectory != null ? subdirectory + "/" : "");
        String filename = "tier_data_" + date.toString() + ".csv";
        String filePath = folderPath + filename;

        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("✅ Created folder: " + folderPath);
            } else {
                throw new IOException("❌ Could not create directory: " + folderPath);
            }
        }

        File file = new File(filePath);
        boolean fileExists = file.exists();
        FileWriter writer = new FileWriter(file, !overwrite);

        try (writer) {
            if (!fileExists || overwrite) {
                writer.write("Tier Name,Patron Count,Revenue (USD),Timestamp\n");
            }

            for (TierSnapshot snapshot : snapshots) {
                writer.write(String.format(
                        "%s,%d,%.2f,%s\n",
                        snapshot.tierName,
                        snapshot.patronCount,
                        snapshot.revenueDollars,
                        date.toString()
                ));
            }
        }
    }

    public static class TierSnapshot {
        public String tierName;
        public int patronCount;
        public double revenueDollars;

        public TierSnapshot(String tierName, int patronCount, double revenueDollars) {
            this.tierName = tierName;
            this.patronCount = patronCount;
            this.revenueDollars = revenueDollars;
        }
    }
}
