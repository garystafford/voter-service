package com.example.voter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private ServicesProperties servicesProperties;

    @Autowired
    private Environment environment;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.printf("Candidates Service Hostname: %s%n", servicesProperties.getCandidateHostname());
        System.out.printf("Candidates Service Port: %s%n", servicesProperties.getCandidatePort());

    }
}
