package com.patreon.api.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Member {

	@Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isActive;
    private String tierId;
    private int pledgeAmountCents;
    private boolean isFollower;
    private boolean isTest=false;


    public Member() {}

    public Member(String id, String firstName, String lastName, String email, boolean isActive, String tierId, int pledgeAmountCents, boolean isFollower, boolean isTest) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isActive = isActive;
        this.tierId = tierId;
        this.pledgeAmountCents = pledgeAmountCents;
        this.isFollower = isFollower;
        this.isTest = isTest;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getTierId() {
        return tierId;
    }

    public void setTierId(String tierId) {
        this.tierId = tierId;
    }

    public int getPledgeAmountCents() {
        return pledgeAmountCents;
    }

    public void setPledgeAmountCents(int pledgeAmountCents) {
        this.pledgeAmountCents = pledgeAmountCents;
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }
    @Override
	public String toString() {
		return "Member [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", isActive=" + isActive + ", tierId=" + tierId + ", pledgeAmountCents=" + pledgeAmountCents
				+ ", isFollower=" + isFollower + ", isTest=" + isTest + "]";
	}

}
