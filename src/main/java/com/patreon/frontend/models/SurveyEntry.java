package com.patreon.frontend.models;

import javafx.beans.property.*;

public class SurveyEntry {

    private final SimpleStringProperty submittedDateTime;
    private final SimpleStringProperty name;
    private final SimpleStringProperty email;
    private final SimpleStringProperty tier;
    private final SimpleStringProperty survey;
    private final SimpleStringProperty comments;

    public SurveyEntry(SimpleStringProperty submittedDateTime, SimpleStringProperty name, SimpleStringProperty email,
                       SimpleStringProperty tier, SimpleStringProperty survey, SimpleStringProperty comments) {
        super();
        this.submittedDateTime = submittedDateTime;
        this.name = name;
        this.email = email;
        this.tier = tier;
        this.survey = survey;
        this.comments = comments;
    }
    
    public SurveyEntry(String submittedDateTime, String  name, String  email,
    		String  tier, String  survey, String  comments) {

    	this.submittedDateTime = new SimpleStringProperty(submittedDateTime);
    	this.name = new SimpleStringProperty(name);
    	this.email = new SimpleStringProperty(email);
    	this.tier = new SimpleStringProperty(tier);
    	this.survey = new SimpleStringProperty(survey);
    	this.comments = new SimpleStringProperty(comments);
    }

    public SimpleStringProperty getSubmittedDateTime() {
        return submittedDateTime;
    }

    public SimpleStringProperty getName() {
        return name;
    }

    public SimpleStringProperty getEmail() {
        return email;
    }

    public SimpleStringProperty getTier() {
        return tier;
    }

    public SimpleStringProperty getSurvey() {
        return survey;
    }

    public SimpleStringProperty getComments() {
        return comments;
    }


}