package com.patreon.frontend.models;

public class RetentionEntry {
    private final String tierName;
    private final String dateLabel;
    private final int patronCount;

    public RetentionEntry(String tierName, String dateLabel, int patronCount) {
        this.tierName = tierName;
        this.dateLabel = dateLabel;
        this.patronCount = patronCount;
    }

    public String getTierName() { return tierName; }
    public String getDateLabel() { return dateLabel; }
    public int getPatronCount() { return patronCount; }
}
