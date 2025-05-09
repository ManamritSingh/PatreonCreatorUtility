package com.patreon.backend;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patreon.backend.models.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByRaffleEligible(String raffleEligible);
}

