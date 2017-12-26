package com.voterapi.voter.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VoterConfig {

    @Bean
    public Queue voteCreatedQueue() {
        return new Queue("vote_created_queue");
    }

    @Bean
    public Queue candidateCreatedQueue() {
        return new Queue("candidate_created_queue");
    }

    @Bean
    public Queue candidateUpdatedQueue() {
        return new Queue("candidate_updated_queue");
    }

    @Bean
    public Queue candidateDeletedQueue() {
        return new Queue("candidate_deleted_queue");
    }
}
