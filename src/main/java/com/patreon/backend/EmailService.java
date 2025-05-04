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

	public void sendEmailToAll(List<String> recipients, String messageBody){
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("introtojava2025@gmail.com");
		message.setSubject("Test");
		message.setText(messageBody);

		for (String recipient : recipients){
			message.setTo(recipient);
			mailSender.send(message);
		}
	}

    public void sendEmailToOne(String recipient, String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("introtojava2025@gmail.com");
        message.setTo(recipient);
        message.setSubject("Test");
        message.setText(messageBody);
        mailSender.send(message);
    }

}
