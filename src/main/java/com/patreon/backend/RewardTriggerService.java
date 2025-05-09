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
    	if (!memberRepository.existsById(newMember.getId())) {
    		memberRepository.save(newMember);
            List<Reward> rewards = rewardRepository.findByTriggerAndStatus("New Subscriber", "Active");

            for (Reward reward : rewards) {
                if (tierMatches(newMember.getTierId(), reward.getRecipients())) {
                	String firstName = newMember.getFirstName();
                    String lastName = newMember.getLastName();
                    String personalizedMessage = replacePlaceholders(reward.getMessage(), firstName, lastName);
                    
                    // Send the email with the personalized message
                    emailService.sendEmailToOne(newMember.getEmail(), reward.getSubject(), personalizedMessage);
                    logAction("New Subscriber", newMember.getEmail());
                }
            }
        }
    }

    public void handleTierChange(Member oldMember, Member updatedMember) {

        if (!oldMember.getTierId().equals(updatedMember.getTierId())) {

            int oldRank = tierRankMap.getOrDefault(oldMember.getTierId(), 0);
            int newRank = tierRankMap.getOrDefault(updatedMember.getTierId(), 0);

            if (newRank > oldRank) {
                List<Reward> rewards = rewardRepository.findByTriggerAndStatus("Upgraded Tier", "Active");


                if (!rewards.isEmpty()) {
                    for (Reward reward : rewards) {
                        if (tierMatches(updatedMember.getTierId(), reward.getRecipients())) {

                        	String firstName = updatedMember.getFirstName();
                            String lastName = updatedMember.getLastName();
                            String personalizedMessage = replacePlaceholders(reward.getMessage(), firstName, lastName);

                            emailService.sendEmailToOne(updatedMember.getEmail(), reward.getSubject(), personalizedMessage);
                            System.out.println("Upgraded Tier reward sent to " + updatedMember.getEmail());
                        }
                    }
                }
            }
        }   
    }

    public void handleUnsubscribe(Member oldMember, Member updatedMember) {
        if (oldMember.isFollower() && !updatedMember.isFollower()) {
        	List<Reward> rewards = rewardRepository.findByTriggerAndStatus("Unsubscribed", "Active");

            for (Reward reward : rewards) {
                if (tierMatches(updatedMember.getTierId(), reward.getRecipients())) {
                	String firstName = updatedMember.getFirstName();
                    String lastName = updatedMember.getLastName();
                    String personalizedMessage = replacePlaceholders(reward.getMessage(), firstName, lastName);

                	emailService.sendEmailToOne(updatedMember.getEmail(), reward.getSubject(), personalizedMessage);
                    logAction("Unsubscribed", updatedMember.getEmail());
                }
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
                String firstName = selectedUser.getFirstName();
                String lastName = selectedUser.getLastName();
                String personalizedMessage = replacePlaceholders(largestIdReward.getMessage(), firstName, lastName);

                try {
                    emailService.sendEmailToOne(selectedUser.getEmail(), largestIdReward.getSubject(), personalizedMessage);
                    logAction("Raffle", selectedUser.getEmail());
                    largestIdReward.setStatus("Sent Successfully");
                    System.out.println("Sent reward with ID " + largestIdReward.getId() + " to " + selectedUser.getEmail());
                } catch (Exception e) {
                    largestIdReward.setStatus("Failed to Send");
                    System.err.println("Failed to send reward with ID " + largestIdReward.getId() + " to " + selectedUser.getEmail());
                    e.printStackTrace();
                }

                // Save the status change
                rewardRepository.save(largestIdReward);
            }
        } else {
            for (Reward reward : rewards) {
                reward.setStatus("No Eligible Members");
                rewardRepository.save(reward);
                System.out.println("No raffle-eligible users found for reward with ID " + reward.getId());
            }
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
    
 // Method to replace placeholders with actual user information
    private String replacePlaceholders(String message, String firstName, String lastName) {
        if (message != null) {
            message = message.replace("{FIRST_NAME}", firstName != null ? firstName : "User");
            message = message.replace("{LAST_NAME}", lastName != null ? lastName : "Member");
        }
        return message;
    }


}


