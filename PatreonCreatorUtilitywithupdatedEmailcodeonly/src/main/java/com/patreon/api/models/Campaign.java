package com.patreon.api.models;

import java.util.ArrayList;
import java.util.List;

public class Campaign {
    public String id;
    public String name;
    public String creationDate;
    public List<Tier> tiers = new ArrayList<>();
}
