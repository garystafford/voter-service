package com.voter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CandidateListService {

    private Environment environment;

    private RabbitTemplate rabbitTemplate;

    private DirectExchange directExchange;

    @Autowired
    public CandidateListService(Environment environment, RabbitTemplate rabbitTemplate, DirectExchange directExchange) {
        this.environment = environment;
        this.rabbitTemplate = rabbitTemplate;
        this.directExchange = directExchange;
    }


    /**
     * Produces HTTP GET request containing election
     * Candidate service returns a list of candidates
     * * @param election
     * @return List of candidates
     */
    public List<String> getCandidatesSyncHttp(String election) {
        List<String> candidatesRemote = new ArrayList<>();
        String candidateServiceHostname = environment.getProperty("services.candidate.host");
        String candidateServicePort = environment.getProperty("services.candidate.port");
        String candidateContextPath = environment.getProperty("services.candidate.context-path");
        String candidateServiceResourceUrl = String.format("http://%s:%s/%s/candidates/summary?election=%s",
                candidateServiceHostname, candidateServicePort, candidateContextPath, election);

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

        List<String> candidatesSorted = candidatesRemote.subList(0, candidatesRemote.size());
        return candidatesSorted;
    }

    /**
     * Produces query message containing election
     * Consumes candidate list based on election query
     * @param election
     * @return List of candidates
     */
    @SuppressWarnings("unchecked")
    public List<String> getCandidatesMessageRpc(String election) {
        System.out.println("Sending RPC request message for list of candidates...");
        String requestMessage = election; //"2016 Presidential Election";
        List<String> candidatesRemote;
        candidatesRemote = (List<String>) rabbitTemplate.convertSendAndReceive(
                directExchange.getName(),"rpc", requestMessage);

        for (String candidate : candidatesRemote) {
            System.out.println(candidate);
        }

        List<String> candidatesSorted = candidatesRemote.subList(0, candidatesRemote.size());
        return candidatesSorted;
    }
}
