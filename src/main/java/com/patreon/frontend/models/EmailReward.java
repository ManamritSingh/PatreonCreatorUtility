package com.patreon.frontend.models;

import java.util.Arrays;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;

public class EmailReward {
	private SimpleStringProperty  message;
	private SimpleStringProperty subject;
	private SimpleStringProperty triggerOpt;
	private List<String> recipients;
	
	public EmailReward(SimpleStringProperty message, SimpleStringProperty subject, SimpleStringProperty trigger, List<String> recepient){
		this.message = message;
		this.subject = subject;
		this.triggerOpt = trigger;
		this.recipients = recepient;
	}
	
	public EmailReward(String message, String subject, String trigger, String recipients) {
        this.message = new SimpleStringProperty(message);
        this.subject = new SimpleStringProperty(subject);
        this.triggerOpt = new SimpleStringProperty(trigger);
        this.recipients = Arrays.asList(recipients.split(","));
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
	public List<String> getRecepients() {
		return recipients;
	}
	public void setMessage(SimpleStringProperty message) {
		this.message = message;
	}
	public void setSubject(SimpleStringProperty subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return "EmailReward [message=" + message + ", subject=" + subject + ", triggerOpt=" + triggerOpt + "]";
	}
}
