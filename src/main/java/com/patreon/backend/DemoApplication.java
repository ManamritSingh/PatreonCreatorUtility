package com.patreon.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EntityScan(basePackages = {"com.patreon.api.models", "com.patreon.backend.models", "com.patreon.utils","com.patreon.backend.chatbot"})
@ComponentScan(basePackages = {
	    "com.patreon.backend",
	    "com.patreon.frontend",
	    "com.patreon.utils"
	})
public class DemoApplication {

    static {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
    }

//    public static void main(String[] args) {
//        SpringApplication.run(DemoApplication.class, args);
//    }
}
