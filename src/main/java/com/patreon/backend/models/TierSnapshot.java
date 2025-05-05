package com.patreon.backend.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="Tier_Snap")
public class TierSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tierName;
    private int patronCount;
    private double revenueDollars;
    private LocalDate timestamp;

    private boolean isMock;  // true = mock, false = real

    // Constructors, Getters, Setters

    public TierSnapshot() {}

    public TierSnapshot(String tierName, int patronCount, double revenueDollars, LocalDate timestamp, boolean isMock) {
        this.tierName = tierName;
        this.patronCount = patronCount;
        this.revenueDollars = revenueDollars;
        this.timestamp = timestamp;
        this.isMock = isMock;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public int getPatronCount() {
        return patronCount;
    }

    public void setPatronCount(int patronCount) {
        this.patronCount = patronCount;
    }

    public double getRevenueDollars() {
        return revenueDollars;
    }

    public void setRevenueDollars(double revenueDollars) {
        this.revenueDollars = revenueDollars;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isMock() {
        return isMock;
    }

    public void setMock(boolean mock) {
        isMock = mock;
    }
}
