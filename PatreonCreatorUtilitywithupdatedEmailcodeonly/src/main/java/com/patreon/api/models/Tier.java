package com.patreon.api.models;

public class Tier {
    private String id;
    private String title;
    private int amountCents;

    public Tier() {
    }

    public Tier(String id, String title, int amountCents) {
        this.id = id;
        this.title = title;
        this.amountCents = amountCents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAmountCents() {
        return amountCents;
    }

    public void setAmountCents(int amountCents) {
        this.amountCents = amountCents;
    }
}
