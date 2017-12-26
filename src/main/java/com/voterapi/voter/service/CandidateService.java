package com.voterapi.voter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voterapi.voter.domain.Candidate;
import com.voterapi.voter.domain.CandidateVoterView;
import com.voterapi.voter.repository.CandidateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@Component
public class CandidateService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private MongoTemplate mongoTemplate;
    private CandidateRepository candidateRepository;
    private Environment environment;

    @Autowired
    public CandidateService(MongoTemplate mongoTemplate,
                            CandidateRepository candidateRepository,
                            Environment environment) {
        this.mongoTemplate = mongoTemplate;
        this.candidateRepository = candidateRepository;
        this.environment = environment;
    }

    /**
     * Retrieves candidates from MongoDB and transforms to voter view
     *
     * @param election
     * @return List of candidates
     */
    public List<CandidateVoterView> getCandidatesQueueDb(String election) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("election").is(election)),
                project("firstName", "lastName", "politicalParty", "election")
                        .andExpression("concat(firstName,' ', lastName)")
                        .as("fullName"),
                sort(Sort.Direction.ASC, "lastName")
        );

        AggregationResults<CandidateVoterView> groupResults
                = mongoTemplate.aggregate(aggregation, Candidate.class, CandidateVoterView.class);

        return groupResults.getMappedResults();
    }

    /**
     * Consumes a new candidate message, deserialize, and save to MongoDB
     *
     * @param candidateCreatedMessage
     */
    @RabbitListener(queues = "#{candidateCreatedQueue.name}")
    public void getCandidateMessage(String candidateCreatedMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        TypeReference<Candidate> mapType = new TypeReference<Candidate>() {
        };

        Candidate candidate = new Candidate();

        try {
            candidate = objectMapper.readValue(candidateCreatedMessage, mapType);
        } catch (IOException e) {
            logger.error(String.valueOf(e));
        }

        candidateRepository.save(candidate);
        if (logger.isDebugEnabled()) {
            logger.debug("Candidate {} saved to MongoDB", candidate.toString());
        }
    }
}