package com.patreon.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.patreon.api.models.Member;

@Controller
public class EmailController {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private MockMemberService mockMemberService;
    
    @Autowired
    private RewardTriggerService rewardTriggerService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String homePage(Model model) {
        List<String> tiers = memberRepository.findAllTiers();
        model.addAttribute("tiers", tiers);
        return "index"; 
    }

    @PostMapping("/send-email")
    public String sendEmailsToTiers(
            @RequestParam("subject") String subject,
            @RequestParam("messageBody") String messageBody,
            @RequestParam(value = "selectedTiers", required = false) List<String> selectedTiers,
            Model model) {

        int sentCount = 0;

        if (selectedTiers != null && !selectedTiers.isEmpty()) {
            List<Member> members;

            if (selectedTiers.contains("All")) {
                // Send to all active members
                members = memberRepository.findByIsActiveTrue();
                model.addAttribute("message", "Sent email to " + members.size() + " members across all tiers.");
            } else {
                // Send to members in selected tiers
                members = memberRepository.findByTierIdIn(new ArrayList<>(selectedTiers));
                model.addAttribute("message", "Sent email to " + members.size() + " members in " + 
                        selectedTiers.size() + " selected tier(s).");
            }

            // Replace placeholders and send emails
            for (Member member : members) {
                String personalizedMessage = replacePlaceholders(messageBody, member);
                emailService.sendEmailToOne(member.getEmail(), subject, personalizedMessage);
                sentCount++;
            }

        } else {
            // Fallback for test members if no tiers are selected
            List<Member> testMembers = memberRepository.findByIsTestTrue();

            for (Member member : testMembers) {
                String personalizedMessage = replacePlaceholders(messageBody, member);
                emailService.sendEmailToOne(member.getEmail(), subject, personalizedMessage);
                sentCount++;
            }

            model.addAttribute("message", "Sent email to " + sentCount + " test members.");
        }

        return "result"; 
    }
    
    @PostMapping("/generate-mock-member")
    public String generateMockMember(Model model) {
        Member mock = mockMemberService.createRandomMember();
        rewardTriggerService.processNewOrUpdatedMember(mock);
        model.addAttribute("message", "Mock member generated and evaluated for rewards.");
        return "result"; 
    }

    // Helper method to replace placeholders
    private String replacePlaceholders(String message, Member member) {
        if (message != null) {
            message = message.replace("{FIRST_NAME}", member.getFirstName() != null ? member.getFirstName() : "User");
            message = message.replace("{LAST_NAME}", member.getLastName() != null ? member.getLastName() : "Member");
        }
        return message;
    }

}
