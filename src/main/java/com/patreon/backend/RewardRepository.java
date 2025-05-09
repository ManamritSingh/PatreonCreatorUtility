package com.patreon.backend;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patreon.backend.models.Reward;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    List<Reward> findByTriggerAndStatus(String trigger, String status);

    // Optional: Find all active rewards
    List<Reward> findByStatus(String status);
}
