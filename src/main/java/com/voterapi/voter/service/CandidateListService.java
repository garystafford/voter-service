package com.voterapi.voter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voterapi.voter.domain.CandidateVoterView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Map;

@Service
public class CandidateListService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Environment environment;

    private RabbitTemplate rabbitTemplate;

    private DirectExchange directExchange;

    @Autowired
    public CandidateListService(Environment environment,
                                RabbitTemplate rabbitTemplate,
                                DirectExchange directExchange) {
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

        return new ArrayList(resources.getContent());
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
        logger.debug("Sending RPC request message for list of candidates...");

        String requestMessage = election;
        String candidates = (String) rabbitTemplate.convertSendAndReceive(
                directExchange.getName(), "rpc", requestMessage);

        TypeReference<Map<String, List<CandidateVoterView>>> mapType =
                new TypeReference<Map<String, List<CandidateVoterView>>>() {};

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, List<CandidateVoterView>> candidatesMap = null;

        try {
            candidatesMap = objectMapper.readValue(candidates, mapType);
        } catch (IOException e) {
            logger.info(String.valueOf(e));
        }

        List<CandidateVoterView> candidatesList  = candidatesMap.get("candidates");
        logger.debug("List of {} candidates received...", candidatesList.size());

        return candidatesList;
    }
}
