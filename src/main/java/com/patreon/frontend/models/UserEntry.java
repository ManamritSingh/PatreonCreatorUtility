package com.patreon.frontend.models;

import javafx.beans.property.SimpleIntegerProperty;
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
	
	public UserEntry(String userID, String  firstName, String lastName, String  email, String active, 
			String tier, String pledge, String  addressName, String  addressLine1, String  addressLine2, 
			String city, String state, String  zipCode, String  country, String  gender,
			String ageRange, String  educationLevel, String  incomeRange, String raffleEligible) {

		this.userID = new SimpleStringProperty(userID);
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		this.email = new SimpleStringProperty(email);
		this.active = new SimpleStringProperty(active);
		this.tier = new SimpleStringProperty(tier);
		this.pledge = new SimpleStringProperty(pledge);
		this.addressName = new SimpleStringProperty(addressName);
		this.addressLine1 = new SimpleStringProperty(addressLine1);
		this.addressLine2 = new SimpleStringProperty(addressLine2);
		this.city = new SimpleStringProperty(city);
		this.state = new SimpleStringProperty(state);
		this.zipCode = new SimpleStringProperty(zipCode);
		this.country = new SimpleStringProperty(country);
		this.gender = new SimpleStringProperty(gender);
		this.ageRange = new SimpleStringProperty(ageRange);
		this.educationLevel = new SimpleStringProperty(educationLevel);
		this.incomeRange = new SimpleStringProperty(incomeRange);
		this.raffleEligible = new SimpleStringProperty(raffleEligible);
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
