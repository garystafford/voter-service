package com.voter.configuration;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VoteConfig {

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("voter.rpc");
    }
}
