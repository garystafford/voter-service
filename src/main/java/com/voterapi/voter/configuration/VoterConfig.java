package com.voterapi.voter.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VoterConfig {

    @Bean
    public Queue candidateQueue() {
        return new Queue("candidates.queue");
    }
}
