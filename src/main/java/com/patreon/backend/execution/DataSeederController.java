package com.patreon.backend.controllers;

import com.patreon.backend.execution.DataSeeder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data")
public class DataSeederController {

    @Autowired
    private DataSeeder dataSeeder;

    @PostMapping("/generate")
    public String generateData(@RequestParam boolean mock) {
        dataSeeder.seedData(mock);
        return mock ? "✅ Fake data generated!" : "✅ Real data generated!";
    }
}
