package com.patreon.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.patreon.api.models.Member;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(MemberRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                // hardcoded test users
                List<Member> testUsers = List.of(
                        new Member("900", "Manamrit", "Singh", "manamritsingh@nyu.edu", true,
                                "test-tier", 0, true, true),

                        new Member("901","Christine", "Wagner",  "caw561@nyu.edu", true,
                                "test-tier", 0, true, true),

                        new Member("902", "Aryan", "Donde", "and8995@nyu.edu", true,
                                "test-tier", 0, true, true)  
                );

                repo.saveAll(testUsers);
            }
        };
    }
}