package com.voterapi.voter.service;

import com.voterapi.voter.domain.Candidate;
import com.voterapi.voter.domain.CandidateVoterView;
import com.voterapi.voter.repository.CandidateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@Service
public class CandidateListService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Environment environment;
    private MongoTemplate mongoTemplate;
    private CandidateRepository candidateRepository;

    @Autowired
    public CandidateListService(Environment environment,
                                MongoTemplate mongoTemplate,
                                CandidateRepository candidateRepository) {
        this.environment = environment;
        this.mongoTemplate = mongoTemplate;
        this.candidateRepository = candidateRepository;
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
}
