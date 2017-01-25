package com.example.voter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CandidateList {

    @Autowired
    private Environment env;

    public List<String> getCandidates() {
        List<String> candidatesSorted = getCandidatesRemote();
        candidatesSorted = candidatesSorted.subList(0, candidatesSorted.size());
        Collections.sort(candidatesSorted);
        return candidatesSorted;
    }

    private List<String> getCandidatesRemote() {
        List<String> candidatesRemote = new ArrayList<>();
        String candidatesHostname = env.getProperty("services.candidates.host");
        String candidatesPort = env.getProperty("services.candidates.port");
        String candidatesResourceUrl = String.format("http://%s:%s/candidates/summary",
                candidatesHostname, candidatesPort);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(candidatesResourceUrl, String.class);

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
}
