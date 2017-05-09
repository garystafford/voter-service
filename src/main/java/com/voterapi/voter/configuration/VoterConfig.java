package com.voterapi.voter.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VoterConfig {

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("voter.rpc");
    }

//    @Bean
//    public FanoutExchange candidateFanoutExchange() {
//        return new FanoutExchange("candidate.fanout");
//    }

//    @Bean
//    public Queue candidateQueue() {
//        return new AnonymousQueue();
//    }

    @Bean
    public Queue candidateQueue() {
        return new Queue("candidates.queue");
    }

//    @Bean
//    public Binding candidateBinding(FanoutExchange candidateFanoutExchange,
//                            Queue candidateQueue) {
//        return BindingBuilder.bind(candidateQueue).to(candidateFanoutExchange);
//    }
}
