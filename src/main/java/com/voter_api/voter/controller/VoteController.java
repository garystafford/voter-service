package com.voter_api.voter.controller;

import com.voter_api.voter.domain.CandidateVoterView;
import com.voter_api.voter.domain.Vote;
import com.voter_api.voter.domain.VoteCount;
import com.voter_api.voter.domain.VoteCountWinner;
import com.voter_api.voter.repository.VoteRepository;
import com.voter_api.voter.service.CandidateListService;
import com.voter_api.voter.service.VoteSeedDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@RestController
public class VoteController {

    private MongoTemplate mongoTemplate;

    private VoteRepository voteRepository;

    private VoteSeedDataService voteSeedDataService;

    private CandidateListService candidateListService;

    @Autowired
    public VoteController(MongoTemplate mongoTemplate, VoteRepository voteRepository,
                          VoteSeedDataService voteSeedDataService, CandidateListService candidateListService) {
        this.mongoTemplate = mongoTemplate;
        this.voteRepository = voteRepository;
        this.voteSeedDataService = voteSeedDataService;
        this.candidateListService = candidateListService;
    }

    @RequestMapping(value = "/candidates/election/{election}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<CandidateVoterView>>> getCandidatesHttp(@PathVariable("election") String election) {
        List<CandidateVoterView> results = candidateListService.getCandidatesSyncHttp(election);
        return new ResponseEntity<>(Collections.singletonMap("candidates", results), HttpStatus.OK);
    }

    @RequestMapping(value = "/candidates/rpc/election/{election}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<CandidateVoterView>>> getCandidatesRpc(@PathVariable("election") String election) {
        List<CandidateVoterView> results = candidateListService.getCandidatesMessageRpc(election);
        return new ResponseEntity<>(Collections.singletonMap("candidates", results), HttpStatus.OK);
    }

    @RequestMapping(value = "/results", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<VoteCount>>> getResults() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("vote").count().as("count"),
                project("count").and("vote").previousOperation(),
                sort(Sort.Direction.DESC, "count")
        );

        AggregationResults<VoteCount> groupResults
                = mongoTemplate.aggregate(aggregation, Vote.class, VoteCount.class);
        List<VoteCount> results = groupResults.getMappedResults();
        return new ResponseEntity<>(Collections.singletonMap("results", results), HttpStatus.OK);
    }

    @RequestMapping(value = "/results/votes", method = RequestMethod.GET)
    public ResponseEntity<VoteCountWinner> getTotalVotes() {

        Query query = new Query();
        query.addCriteria(Criteria.where("vote").exists(true));

        Long groupResults =
                mongoTemplate.count(query, Vote.class);
        VoteCountWinner result = new VoteCountWinner(groupResults.intValue());

        return ResponseEntity.status(HttpStatus.OK).body(result); // return 200 with payload
    }

    @RequestMapping(value = "/winners", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<VoteCount>>> getWinners() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("vote").count().as("count"),
                match(Criteria.where("count").is(getWinnersVotesInt())),
                project("count").and("vote").previousOperation(),
                sort(Sort.Direction.ASC, "vote")
        );

        AggregationResults<VoteCount> groupResults
                = mongoTemplate.aggregate(aggregation, Vote.class, VoteCount.class);
        List<VoteCount> results = groupResults.getMappedResults();
        return new ResponseEntity<>(Collections.singletonMap("results", results), HttpStatus.OK);
    }

    @RequestMapping(value = "/winners/votes", method = RequestMethod.GET)
    public ResponseEntity<VoteCountWinner> getWinnersVotes() {

        VoteCountWinner result = new VoteCountWinner(getWinnersVotesInt());

        return ResponseEntity.status(HttpStatus.OK).body(result); // return 200 with payload
    }

    private int getWinnersVotesInt() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("vote").count().as("count"),
                project("count"),
                sort(Sort.Direction.DESC, "count"),
                limit(1)
        );

        AggregationResults<VoteCountWinner> groupResults =
                mongoTemplate.aggregate(aggregation, Vote.class, VoteCountWinner.class);
        if (groupResults.getMappedResults().isEmpty()) {
            return 0;
        }
        int result = groupResults.getMappedResults().get(0).getCount();

        return result;
    }

    @RequestMapping(value = "/simulation/election/{election}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getSimulationHttp(@PathVariable("election") String election) {

        voteRepository.deleteAll();
        voteSeedDataService.setRandomVotesHttp(election);
        voteRepository.save(voteSeedDataService.getVotes());
        Map<String, String> result = new HashMap<>();
        result.put("message", "Simulation data created!");
        return ResponseEntity.status(HttpStatus.OK).body(result); // return 200 with payload
    }

    @RequestMapping(value = "/simulation/rpc/election/{election}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getSimulationRpc(@PathVariable("election") String election) {

        voteRepository.deleteAll();
        voteSeedDataService.setRandomVotesRpc(election);
        voteRepository.save(voteSeedDataService.getVotes());
        Map<String, String> result = new HashMap<>();
        result.put("message", "Simulation data created using RPC!");
        return ResponseEntity.status(HttpStatus.OK).body(result); // return 200 with payload
    }

    // used by unit tests to create a known data set
    public void getSimulation(Map candidates, String election) {

        voteRepository.deleteAll();
        voteSeedDataService.votesFromMap(candidates, election);
        voteRepository.save(voteSeedDataService.getVotes());
    }
}
