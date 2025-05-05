package com.patreon.backend;

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
            @RequestParam(value = "selectedTiers", required = false) String[] selectedTiers,
            Model model) {
        
        int sentCount = 0;
        
        if (selectedTiers != null && selectedTiers.length > 0) {
            Set<String> tierSet = Arrays.stream(selectedTiers).collect(Collectors.toSet());
            
            List<Member> members;
            
            if (tierSet.contains("All")) {
                members = memberRepository.findByIsActiveTrue();
                model.addAttribute("message", "Sent email to " + members.size() + 
                        " members across all tiers.");
            } else {
                members = memberRepository.findByTierIdIn(tierSet);
                model.addAttribute("message", "Sent email to " + members.size() + " members in " + 
                        tierSet.size() + " selected tier(s).");
            }
            
            for (Member member : members) {
                emailService.sendEmailToOne(member.getEmail(), subject, messageBody);
                sentCount++;
            }
            
        } else {
            List<Member> testMembers = memberRepository.findByIsTestTrue();
            
            for (Member member : testMembers) {
                emailService.sendEmailToOne(member.getEmail(), subject, messageBody);
                sentCount++;
            }
            
            model.addAttribute("message", "Sent email to " + sentCount + " test members.");
        }
        
        return "result"; 
    }
}