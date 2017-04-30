package com.voter_api.voter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voter_api.voter.domain.CandidateVoterView;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
     *
     * @return List of candidates
     */
    public List<CandidateVoterView> getCandidatesSyncHttp(String election) {
        String candidateServiceHostname = environment.getProperty("services.candidate.host");
        String candidateServicePort = environment.getProperty("services.candidate.port");
        String candidateContextPath = environment.getProperty("services.candidate.context-path");
        String candidateServiceResourceUrl = String.format("http://%s:%s/%s/candidates/search/findByElectionContains?election=%s&projection=candidateVoterView",
                candidateServiceHostname, candidateServicePort, candidateContextPath, election);

        RestTemplate restTemplate = restTemplate();

        ResponseEntity<PagedResources<CandidateVoterView>> responseEntity = restTemplate.exchange(
                candidateServiceResourceUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<PagedResources<CandidateVoterView>>() {
                });
        PagedResources<CandidateVoterView> resources = responseEntity.getBody();
        List<CandidateVoterView> candidates = new ArrayList(resources.getContent());

        return candidates;
    }

    private RestTemplate restTemplate() {
        // reference: http://izeye.blogspot.com/2015/01/consume-spring-data-rest-hateoas-hal.html
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new Jackson2HalModule());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
        converter.setObjectMapper(mapper);
        return new RestTemplate(Arrays.asList(converter));
    }

    /**
     * Produces query message containing election
     * Consumes candidate list based on election query
     *
     * @param election
     * @return List of candidates
     */
    @SuppressWarnings("unchecked")
    public List<CandidateVoterView> getCandidatesMessageRpc(String election) {
        System.out.println("Sending RPC request message for list of candidates...");
        String requestMessage = election; //"2016 Presidential Election";
        String candidates = (String) rabbitTemplate.convertSendAndReceive(
                directExchange.getName(), "rpc", requestMessage);
        TypeReference<List<CandidateVoterView>> mapType =
                new TypeReference<List<CandidateVoterView>>() {};
        ObjectMapper objectMapper = new ObjectMapper();
        List<CandidateVoterView> candidatesList = null;
        try {
            candidatesList = objectMapper.readValue(candidates, mapType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("List of " + candidatesList.size() + " candidates received...");
        return candidatesList;
    }
}
