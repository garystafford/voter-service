package com.example.voter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "services")
public class ServicesProperties {

    @Value("${services.candidateHostname}")
    private String candidateHostname;

    @Value("${services.candidatePort}")
    private String candidatePort;

    public String getCandidateHostname() {
        return candidateHostname;
    }

    public void setCandidateHostname(String candidateHostname) {
        this.candidateHostname = candidateHostname;
    }

    public String getCandidatePort() {
        return candidatePort;
    }

    public void setCandidatePort(String candidatePort) {
        this.candidatePort = candidatePort;
    }

}
