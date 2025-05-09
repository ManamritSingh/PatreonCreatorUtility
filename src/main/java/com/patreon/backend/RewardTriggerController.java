/*package com.patreon.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.patreon.api.models.Member;

@Controller
public class RewardTriggerController {

    @Autowired
    private MockMemberService mockService;

    @Autowired
    private RewardTriggerService rewardTriggerService;

    @PostMapping("/generate-mock-member")
    public String generateMockMember(Model model) {
        Member mockMember = mockService.createRandomMember();
        rewardTriggerService.processNewOrUpdatedMember(mockMember);
        model.addAttribute("message", "Mock member added and checked for rewards.");
        return "result"; // or redirect to /email-rewards or wherever your view is
    }
}*/

