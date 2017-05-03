package com.voter_api.voter.configuration;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VoterConfig {

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("voter.rpc");
    }
}
