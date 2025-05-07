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
                // 1. Load from CSV
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        getClass().getResourceAsStream("/members.csv")))) {

                    reader.lines().skip(1).forEach(line -> {
                        String[] parts = line.split(",", -1); // preserve empty strings

                        String id = parts[0];
                        String firstName = parts[1];
                        String lastName = parts[2];
                        String email = parts[3];
                        boolean isActive = parts[4].trim().equalsIgnoreCase("true");
                        String tier = parts[5];
                        int pledge = (int) (Double.parseDouble(parts[6]) * 100);
                        String addressName = parts[7];
                        String addressLine1 = parts[8];
                        String addressLine2 = parts[9];
                        String city = parts[10];
                        String state = parts[11];
                        String zip = parts[12];
                        String country = parts[13];
                        String gender = parts[14];
                        String ageRange = parts[15];
                        String education = parts[16];
                        String income = parts[17];
                        boolean isFollower = parts[18].trim().equalsIgnoreCase("true");

                        boolean isTest = false;  // default for CSV entries

                        Member m = new Member(id, firstName, lastName, email, isActive, tier, pledge,
                                addressName, addressLine1, addressLine2, city, state, zip, country,
                                gender, ageRange, education, income, isFollower, isTest);

                        repo.save(m);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 2. Add hardcoded test users
                List<Member> testUsers = List.of(
                        new Member("900", "Manamrit", "Singh", "manamritsingh@nyu.edu", true,
                                "test-tier", 0, "Test Addr", "Line1", "Line2", "City", "TS", "00001", "Testland",
                                "Other", "18-24", "College", "<10k", true, true),

                        new Member("901","Christine", "Wagner",  "caw561@nyu.edu", true,
                                "test-tier", 0, "Test Addr", "Line1", "Line2", "City", "TS", "00002", "Testland",
                                "Other", "25-34", "Graduate", "<20k", true, true),

                        new Member("902", "Aryan", "Donde", "and8995@nyu.edu", true,
                                "test-tier", 0, "Test Addr", "Line1", "Line2", "City", "TS", "00003", "Testland",
                                "Other", "35-44", "PhD", "<30k", true, true)
                );

                repo.saveAll(testUsers);
            }
        };
    }
}