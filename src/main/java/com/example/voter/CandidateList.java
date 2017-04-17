package com.example.voter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class CandidateList {

    @Autowired
    private Environment env;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<String> getCandidates() {
        List<String> candidatesSorted = getCandidatesRemote();
        List<String> candidatesMessageSorted = getCandidatesMessage();
        candidatesSorted = candidatesSorted.subList(0, candidatesSorted.size());
        Collections.sort(candidatesSorted);
        return candidatesSorted;
    }

    private List<String> getCandidatesRemote() {
        List<String> candidatesRemote = new ArrayList<>();
        String candidateServiceHostname = env.getProperty("services.candidate.host");
        String candidateServicePort = env.getProperty("services.candidate.port");
        String candidateServiceResourceUrl = String.format("http://%s:%s/candidates/summary",
                candidateServiceHostname, candidateServicePort);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(candidateServiceResourceUrl, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(responseEntity.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayNode candidatesArray = (ArrayNode) rootNode.get("candidates");

        for (int i = 0; i < candidatesArray.size(); i++) {
            JsonNode jsonNode = candidatesArray.get(i);
            if (jsonNode.isTextual()) {
                candidatesRemote.add(jsonNode.asText());
            }
        }

        return candidatesRemote;
    }

    private List<String> getCandidatesMessage() {
        System.out.println("Sending request for candidates...");
        String correlationId = UUID.randomUUID().toString();
        List<String> message = new ArrayList<>();
        message.add(0, correlationId);
        message.add(1, Application.replyTo);
        CorrelationData correlationData = new CorrelationData(correlationId);

        rabbitTemplate.convertSendAndReceive(Application.requestQueueName, message, correlationData);
        List<String> candidatesRemote = new ArrayList<>();
        return candidatesRemote;
    }
}
