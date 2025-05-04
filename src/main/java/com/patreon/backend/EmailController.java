package com.patreon.backend;

import com.patreon.api.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EmailController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String homePage() {
        return "index"; // loads index.html from src/main/resources/templates/
    }

    @PostMapping("/send-email")
    public String sendEmailsToAllTest(@RequestParam("messageBody") String messageBody, Model model) {
        List<Member> TestMembers = memberRepository.findByIsTestTrue();
        int sentCount = 0;

        for (Member member : TestMembers) {
            emailService.sendEmailToOne(member.getEmail(), messageBody);
            sentCount++;
        }

        model.addAttribute("message", "Sent email to " + sentCount + " active members.");
        return "result"; // loads result.html
    }
}
