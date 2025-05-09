package com.patreon.backend;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.patreon.api.models.Member;
import com.patreon.backend.models.Reward;
import com.patreon.backend.models.User;

@Service
public class RewardTriggerService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RewardRepository rewardRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    
    private Map<String, Integer> tierRankMap = Map.of(
            "Free", 1,
            "Heroes", 2,
            "SuperHeros", 3,
            "Legends", 4
        );

    public void processNewOrUpdatedMember(Member newMember) {
        Optional<Member> existingMember = memberRepository.findById(newMember.getId());
        System.out.println("Processing member: Existing: " + existingMember + "newMember: " + newMember);
        if (existingMember.isPresent()) {
        	Member oldMember = copyMember(existingMember.get());
            memberRepository.save(newMember); // Save updated member info

            handleTierChange(oldMember, newMember);
            handleUnsubscribe(oldMember, newMember);
        } else {
            handleNewMember(newMember);
        }
    }
    
    public void handleNewMember(Member newMember) {
    	System.out.println("handleNewMember called on" + newMember.getId());
    	if (!memberRepository.existsById(newMember.getId())) {
    		System.out.println("Inside IF block of if the repository does note have member" + newMember.getId());
    		memberRepository.save(newMember);
            System.out.println("Checking for New Subscriber rewards...");
            List<Reward> rewards = rewardRepository.findByTriggerAndStatus("New Subscriber", "Active");
            System.out.println("Found rewards: " + rewards.size());


            for (Reward reward : rewards) {
                if (tierMatches(newMember.getTierId(), reward.getRecipients())) {
                    emailService.sendEmailToOne(newMember.getEmail(), reward.getSubject(), reward.getMessage());
                    logAction("New Subscriber", newMember.getEmail());
                }
            }
        }
    }

    public void handleTierChange(Member oldMember, Member updatedMember) {
        System.out.println("handleTierChange called on " + oldMember + " updated to " + updatedMember);

        // Check if the tier has actually changed
        if (!oldMember.getTierId().equals(updatedMember.getTierId())) {
            System.out.println("Tier change inside if block");

            // Get the ranks from the tierRankMap, defaulting to 0 if not found
            int oldRank = tierRankMap.getOrDefault(oldMember.getTierId(), 0);
            int newRank = tierRankMap.getOrDefault(updatedMember.getTierId(), 0);

            // Only process if the new rank is greater than the old rank
            if (newRank > oldRank) {
                System.out.println("Checking for Upgraded Tier rewards...");

                // Fetch rewards for "Upgraded Tier" trigger and "Active" status
                List<Reward> rewards = rewardRepository.findByTriggerAndStatus("Upgraded Tier", "Active");
                System.out.println("Found rewards: " + rewards.size());

                // If rewards exist, check if they match the member's tier
                if (!rewards.isEmpty()) {
                    for (Reward reward : rewards) {
                        // Check if the reward applies to the member's new tier
                        if (tierMatches(updatedMember.getTierId(), reward.getRecipients())) {
                            // Send the email reward to the member
                            emailService.sendEmailToOne(updatedMember.getEmail(), reward.getSubject(), reward.getMessage());
                            System.out.println("Upgraded Tier reward sent to " + updatedMember.getEmail());
                        }
                    }
                } else {
                    System.out.println("No active rewards found for upgraded tier.");
                }
            } else {
                System.out.println("New rank is not higher than old rank. No action taken.");
            }
        } else {
            System.out.println("Tier did not change. No action taken.");
        }
    }

    public void handleUnsubscribe(Member oldMember, Member updatedMember) {
    	System.out.println("handleTierChange called on" + oldMember + " updated to " + updatedMember);
        if (oldMember.isFollower() && !updatedMember.isFollower()) {
        	System.out.println("Unsubscribe inside if block");
        	System.out.println("Checking for Unsubscribed rewards...");
        	List<Reward> rewards = rewardRepository.findByTriggerAndStatus("Unsubscribed", "Active");
        	System.out.println("Found rewards: " + rewards.size());

            for (Reward reward : rewards) {
                if (tierMatches(updatedMember.getTierId(), reward.getRecipients())) {
                    emailService.sendEmailToOne(updatedMember.getEmail(), reward.getSubject(), reward.getMessage());
                    logAction("Unsubscribed", updatedMember.getEmail());
                }
            }
        }
    }

    public void handleSurveyCompletion(String memberEmail, String tierId) {
    	System.out.println("handleSurveyCompletion called on" + memberEmail);
        List<Reward> rewards = rewardRepository.findByTriggerAndStatus("Survey Completion", "Active");

        for (Reward reward : rewards) {
            if (tierMatches(tierId, reward.getRecipients())) {
                emailService.sendEmailToOne(memberEmail, reward.getSubject(), reward.getMessage());
                logAction("Survey Completion", memberEmail);
            }
        }
    }

    public void triggerRaffleReward() {
        System.out.println("triggerRaffleReward called");

        List<Reward> rewards = rewardRepository.findByTriggerAndStatus("Raffle", "Active");

        if (rewards.isEmpty()) {
            System.out.println("No active rewards for Raffle.");
            return;
        }

        List<User> eligibleUsers = userRepository.findByRaffleEligible("Yes");

        if (!eligibleUsers.isEmpty()) {
            Random random = new Random();
            User selectedUser = eligibleUsers.get(random.nextInt(eligibleUsers.size()));

            // Find the reward with the largest ID
            Reward largestIdReward = rewards.stream()
                .max(Comparator.comparingInt(Reward::getId))
                .orElse(null);

            if (largestIdReward != null && tierMatches(selectedUser.getTierId(), largestIdReward.getRecipients())) {
                emailService.sendEmailToOne(selectedUser.getEmail(), largestIdReward.getSubject(), largestIdReward.getMessage());
                logAction("Raffle", selectedUser.getEmail());
                System.out.println("Sent reward with ID " + largestIdReward.getId() + " to " + selectedUser.getEmail());
            }
        } else {
            System.out.println("No raffle-eligible users found.");
        }
    }



    private boolean tierMatches(String memberTier, String rewardRecipients) {
        System.out.println("tierMatches being called");
        if (rewardRecipients == null || rewardRecipients.isBlank()) {
            System.out.println("Checking tier match: memberTier=" + memberTier + ", rewardRecipients=" + rewardRecipients);
            return false;
        }

        // Normalize and check for "All"
        if (rewardRecipients.trim().equalsIgnoreCase("All")) {
            System.out.println("Tier match because recipients=All");
            return true;
        }

        // Normalize both sides
        String normalizedMemberTier = memberTier.trim();

        Set<String> allowedTiers = Arrays.stream(rewardRecipients.split(","))
                                         .map(String::trim)
                                         .collect(Collectors.toSet());

        boolean match = allowedTiers.contains(normalizedMemberTier);
        System.out.println("Tier match? " + match + " for tier: " + normalizedMemberTier + " in " + allowedTiers);
        return match;
    }



    private void logAction(String trigger, String recipient) {
        System.out.println("Triggered [" + trigger + "] reward email for: " + recipient);
    }
    
    private Member copyMember(Member original) {
        Member copy = new Member();
        copy.setId(original.getId());
        copy.setFirstName(original.getFirstName());
        copy.setLastName(original.getLastName());
        copy.setEmail(original.getEmail());
        copy.setActive(original.isActive());
        copy.setTierId(original.getTierId());
        copy.setPledgeAmountCents(original.getPledgeAmountCents());
        copy.setFollower(original.isFollower());
        copy.setTest(original.isTest());
        return copy;
    }

}


