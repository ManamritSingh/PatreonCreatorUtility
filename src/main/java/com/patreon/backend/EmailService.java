package com.patreon.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EmailService{
	@Autowired
	private JavaMailSender mailSender;

	public void sendEmailToAll(List<String> recipients, String subject, String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("introtojava2025@gmail.com");
        message.setSubject(subject);
        message.setText(messageBody);
        
        for (String recipient : recipients) {
            message.setTo(recipient);
            mailSender.send(message);
        }
    }

	public void sendEmailToOne(String recipient, String subject, String messageBody) {
	    try {
	        System.out.println("Attempting to send email to: " + recipient);
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom("introtojava2025@gmail.com");
	        message.setTo(recipient);
	        message.setSubject(subject);
	        message.setText(messageBody);
	        mailSender.send(message);
	        System.out.println("Email successfully sent to: " + recipient);
	    } catch (Exception e) {
	        System.err.println("Failed to send email to " + recipient);
	        e.printStackTrace();
	    }
	}

}
