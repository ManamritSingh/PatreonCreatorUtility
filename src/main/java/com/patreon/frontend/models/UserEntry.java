package com.patreon.frontend.models;

import javafx.beans.property.SimpleStringProperty;

public class UserEntry {
	private final SimpleStringProperty userID;
	private final SimpleStringProperty firstName;
	private final SimpleStringProperty lastName;
	private final SimpleStringProperty email;
	private final SimpleStringProperty active;
	private final SimpleStringProperty tier;
	private final SimpleStringProperty pledge;
	private final SimpleStringProperty addressName;
	private final SimpleStringProperty addressLine1;
	private final SimpleStringProperty addressLine2;
	private final SimpleStringProperty city;
	private final SimpleStringProperty state;
	private final SimpleStringProperty zipCode;
	private final SimpleStringProperty country;
	private final SimpleStringProperty gender;
	private final SimpleStringProperty ageRange;
	private final SimpleStringProperty educationLevel;
	private final SimpleStringProperty incomeRange;
	private final SimpleStringProperty raffleEligible;
	
	public UserEntry(SimpleStringProperty userID, SimpleStringProperty firstName, SimpleStringProperty lastName,
			SimpleStringProperty email, SimpleStringProperty active, SimpleStringProperty tier,
			SimpleStringProperty pledge, SimpleStringProperty addressName, SimpleStringProperty addressLine1,
			SimpleStringProperty addressLine2, SimpleStringProperty city, SimpleStringProperty state,
			SimpleStringProperty zipCode, SimpleStringProperty country, SimpleStringProperty gender,
			SimpleStringProperty ageRange, SimpleStringProperty educationLevel, SimpleStringProperty incomeRange,
			SimpleStringProperty raffleEligible) {
		super();
		this.userID = userID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.active = active;
		this.tier = tier;
		this.pledge = pledge;
		this.addressName = addressName;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
		this.country = country;
		this.gender = gender;
		this.ageRange = ageRange;
		this.educationLevel = educationLevel;
		this.incomeRange = incomeRange;
		this.raffleEligible = raffleEligible;
	}

	public SimpleStringProperty getUserID() {
		return userID;
	}

	public SimpleStringProperty getFirstName() {
		return firstName;
	}

	public SimpleStringProperty getLastName() {
		return lastName;
	}

	public SimpleStringProperty getEmail() {
		return email;
	}

	public SimpleStringProperty getActive() {
		return active;
	}

	public SimpleStringProperty getTier() {
		return tier;
	}

	public SimpleStringProperty getPledge() {
		return pledge;
	}

	public SimpleStringProperty getAddressName() {
		return addressName;
	}

	public SimpleStringProperty getAddressLine1() {
		return addressLine1;
	}

	public SimpleStringProperty getAddressLine2() {
		return addressLine2;
	}

	public SimpleStringProperty getCity() {
		return city;
	}

	public SimpleStringProperty getState() {
		return state;
	}

	public SimpleStringProperty getZipCode() {
		return zipCode;
	}

	public SimpleStringProperty getCountry() {
		return country;
	}

	public SimpleStringProperty getGender() {
		return gender;
	}

	public SimpleStringProperty getAgeRange() {
		return ageRange;
	}

	public SimpleStringProperty getEducationLevel() {
		return educationLevel;
	}

	public SimpleStringProperty getIncomeRange() {
		return incomeRange;
	}

	public SimpleStringProperty getRaffleEligible() {
		return raffleEligible;
	}
	
	
}
