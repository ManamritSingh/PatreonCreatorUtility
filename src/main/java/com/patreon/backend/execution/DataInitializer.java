package com.patreon.backend.execution;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
//import com.patreon.backend.execution.DataSeeder;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DataSeeder seeder;

    public DataInitializer(DataSeeder seeder) {
        this.seeder = seeder;
    }

    @Override
    public void run(String... args) {
        try {
            boolean useRealData = true; // change this flag as needed or fetch from ENV
            seeder.seedData(useRealData);
        } catch (Exception e) {
            System.err.println("❌ Data seeding failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
