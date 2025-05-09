package com.patreon.backend;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.patreon.api.models.Member;

@Service
public class MockMemberService {

    private final Random random = new Random();

    @Autowired
    private RewardTriggerService rewardTriggerService;

    private static final List<String> IDS = List.of("000001", "000002", "000003", "000004", "000005");
    private static final List<String> FIRST_NAMES = List.of("Bon");
    private static final List<String> LAST_NAMES = List.of("Jovi");
    private static final List<String> EMAILS = List.of("introjava2025@gmail.com");
    private static final List<String> TIERS = List.of("Free", "Heroes", "SuperHeros", "Legends");
    private static final List<Integer> PLEDGE_AMOUNTS = List.of(1, 2, 3, 4, 5);
    private static final List<Boolean> BOOLEAN_VALUES = List.of(true, false);

    public Member createRandomMember() {
        String id = IDS.get(random.nextInt(IDS.size()));
        String firstName = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
        String lastName = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
        String email = EMAILS.get(random.nextInt(EMAILS.size()));
        boolean isActive = BOOLEAN_VALUES.get(random.nextInt(BOOLEAN_VALUES.size()));
        String tierId = TIERS.get(random.nextInt(TIERS.size()));
        int pledgeAmountCents = PLEDGE_AMOUNTS.get(random.nextInt(PLEDGE_AMOUNTS.size()));
        boolean isFollower = BOOLEAN_VALUES.get(random.nextInt(BOOLEAN_VALUES.size()));
        
        Member member = new Member(id, firstName, lastName, email, isActive, tierId, pledgeAmountCents, isFollower, true);
        rewardTriggerService.processNewOrUpdatedMember(member);

        return member;
    }
}

