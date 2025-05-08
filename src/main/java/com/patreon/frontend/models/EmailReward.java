package com.patreon.frontend.models;

import java.util.Arrays;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmailReward {
	private final SimpleIntegerProperty id = new SimpleIntegerProperty();
	private SimpleStringProperty  message;
	private SimpleStringProperty subject;
	private SimpleStringProperty triggerOpt;
	private List<String> recipients;
	private SimpleStringProperty status;
	
	public EmailReward(SimpleStringProperty message, SimpleStringProperty subject, SimpleStringProperty trigger, List<String> recepient, SimpleStringProperty status){
		this.message = message;
		this.subject = subject;
		this.triggerOpt = trigger;
		this.recipients = recepient;
		this.status = status;
	}
	
	public EmailReward(String message, String subject, String trigger, String recipients, String status) {
        this.message = new SimpleStringProperty(message);
        this.subject = new SimpleStringProperty(subject);
        this.triggerOpt = new SimpleStringProperty(trigger);
        this.recipients = Arrays.asList(recipients.split(","));
        this.status = new SimpleStringProperty(status);
	}
	
	public EmailReward(int id, String message, String subject, String trigger, String recipients, String status) {
	    this.id.set(id);
	    this.message = new SimpleStringProperty(message);
	    this.subject = new SimpleStringProperty(subject);
	    this.triggerOpt = new SimpleStringProperty(trigger);
	    this.recipients = Arrays.asList(recipients.split(","));
	    this.status = new SimpleStringProperty(status);
	}

	
	public SimpleStringProperty getMessage() {
		return message;
	}
	public SimpleStringProperty getSubject() {
		return subject;
	}
	public SimpleStringProperty getTriggerOpt() {
		return triggerOpt;
	}
	public SimpleStringProperty getStatus() {
		return status;
	}
	public List<String> getRecepients() {
		return recipients;
	}
	public void setMessage(SimpleStringProperty message) {
		this.message = message;
	}
	public void setSubject(SimpleStringProperty subject) {
		this.subject = subject;
	}
	public void setStatus(String status) {
		this.status = new SimpleStringProperty(status);
	}

	public int getId() {
		return id.get();
	}

	public SimpleIntegerProperty idProperty() {
		return id;
	}

	public void setId(int id) {
		this.id.set(id);
	}

	@Override
	public String toString() {
		return "EmailReward [message=" + message + ", subject=" + subject + ", triggerOpt=" + triggerOpt + ", status=" + status + "]";
	}
}
