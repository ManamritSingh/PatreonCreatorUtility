package com.patreon.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EntityScan(basePackages = {"com.patreon.api.models", "com.patreon.backend.models", "com.patreon.utils" })
public class DemoApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

		// Propagate .env values to system properties so Spring can access them
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

//		SpringApplication.run(DemoApplication.class, args);
	}
}
