package com.patreon.api.models;

public class Member {
    public String id;
    public String fullName;
    public String email;
    public String tierId;       // linked to Tier.id
    public int pledgeAmountCents;
    public boolean isActive;
    public boolean isFollower;

}
