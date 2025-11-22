package org.dkb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main entry point for the Spring Boot application.
 * The @SpringBootApplication annotation enables:
 * 1. Auto-Configuration: Automatically configures Spring based on project dependencies (e.g., JPA...).
 * 2. Component Scanning: Finds and registers all components (@Controller, @Service, @Repository).
 */
@SpringBootApplication
@EnableCaching
public class Shortify {

    public static void main(String[] args) {
        SpringApplication.run(Shortify.class, args);
    }

}