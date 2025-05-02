package com.patreon.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/send-email")
    public String sendEmail(@RequestParam("messageBody") String messageBody, Model model) {
        emailService.sendEmailToAll(userRepository.findAllEmails(), messageBody);
        model.addAttribute("message", "Emails sent successfully!");
        return "result";
    }
}

