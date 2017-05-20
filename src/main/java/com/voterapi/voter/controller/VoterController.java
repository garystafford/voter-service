package com.voterapi.voter.controller;

import com.voterapi.voter.domain.CandidateVoterView;
import com.voterapi.voter.domain.Vote;
import com.voterapi.voter.domain.VoteCount;
import com.voterapi.voter.domain.VoteCountWinner;
import com.voterapi.voter.repository.CandidateRepository;
import com.voterapi.voter.repository.VoterRepository;
import com.voterapi.voter.service.CandidateListService;
import com.voterapi.voter.service.VoterSeedDataService;
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
public class VoterController {

    private MongoTemplate mongoTemplate;

    private VoterRepository voterRepository;

    private CandidateRepository candidateRepository;

    private VoterSeedDataService voterSeedDataService;

    private CandidateListService candidateListService;

    @Autowired
    public VoterController(MongoTemplate mongoTemplate,
                           VoterRepository voterRepository,
                           CandidateRepository candidateRepository,
                           VoterSeedDataService voterSeedDataService,
                           CandidateListService candidateListService) {
        this.mongoTemplate = mongoTemplate;
        this.candidateRepository = candidateRepository;
        this.voterRepository = voterRepository;
        this.voterSeedDataService = voterSeedDataService;
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

    @RequestMapping(value = "/candidates/db/election/{election}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<CandidateVoterView>>> getCandidatesDb(@PathVariable("election") String election) {
        List<CandidateVoterView> results = candidateListService.getCandidatesQueueDb(election);
        return new ResponseEntity<>(Collections.singletonMap("candidates", results), HttpStatus.OK);
    }

    @RequestMapping(value = "/votes/drop", method = RequestMethod.POST)
    public ResponseEntity<Void> deleteAllVotes() {

        voterRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/candidates/drop", method = RequestMethod.POST)
    public ResponseEntity<Void> deleteAllCandidates() {

        candidateRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/results", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<VoteCount>>> getResults() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("candidate").count().as("votes"),
                project("votes").and("candidate").previousOperation(),
                sort(Sort.Direction.DESC, "votes")
        );

        AggregationResults<VoteCount> groupResults
                = mongoTemplate.aggregate(aggregation, Vote.class, VoteCount.class);
        List<VoteCount> results = groupResults.getMappedResults();
        return new ResponseEntity<>(Collections.singletonMap("results", results), HttpStatus.OK);
    }

    @RequestMapping(value = "/results/votes", method = RequestMethod.GET)
    public ResponseEntity<VoteCountWinner> getTotalVotes() {

        Query query = new Query();
        query.addCriteria(Criteria.where("candidate").exists(true));

        Long groupResults =
                mongoTemplate.count(query, Vote.class);
        VoteCountWinner result = new VoteCountWinner(groupResults.intValue());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @RequestMapping(value = "/winners", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<VoteCount>>> getWinners() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("candidate").count().as("votes"),
                match(Criteria.where("votes").is(getWinnersVotesInt())),
                project("votes").and("candidate").previousOperation(),
                sort(Sort.Direction.ASC, "candidate")
        );

        AggregationResults<VoteCount> groupResults
                = mongoTemplate.aggregate(aggregation, Vote.class, VoteCount.class);
        List<VoteCount> results = groupResults.getMappedResults();
        return new ResponseEntity<>(Collections.singletonMap("results", results), HttpStatus.OK);
    }

    @RequestMapping(value = "/winners/votes", method = RequestMethod.GET)
    public ResponseEntity<VoteCountWinner> getWinnersVotes() {

        VoteCountWinner result = new VoteCountWinner(getWinnersVotesInt());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    private int getWinnersVotesInt() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("candidate").count().as("votes"),
                project("votes"),
                sort(Sort.Direction.DESC, "votes"),
                limit(1)
        );

        AggregationResults<VoteCountWinner> groupResults =
                mongoTemplate.aggregate(aggregation, Vote.class, VoteCountWinner.class);
        if (groupResults.getMappedResults().isEmpty()) {
            return 0;
        }

        return groupResults.getMappedResults().get(0).getVotes();
    }

    @RequestMapping(value = "/simulation/election/{election}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getSimulationHttp(@PathVariable("election") String election) {

        voterRepository.deleteAll();
        voterSeedDataService.setRandomVotesHttp(election);
        voterRepository.save(voterSeedDataService.getVotes());
        Map<String, String> result = new HashMap<>();
        result.put("message", "Simulation data created!");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @RequestMapping(value = "/simulation/rpc/election/{election}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getSimulationRpc(@PathVariable("election") String election) {

        voterRepository.deleteAll();
        voterSeedDataService.setRandomVotesRpc(election);
        voterRepository.save(voterSeedDataService.getVotes());
        Map<String, String> result = new HashMap<>();
        result.put("message", "Simulation data created using RPC!");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @RequestMapping(value = "/simulation/db/election/{election}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getSimulationDb(@PathVariable("election") String election) {

        voterRepository.deleteAll();
        voterSeedDataService.setRandomVotesDb(election);
        voterRepository.save(voterSeedDataService.getVotes());
        Map<String, String> result = new HashMap<>();
        result.put("message", "Simulation data created using eventual consistency!");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // used by unit tests to create a known data set
    public void getSimulation(Map candidates, String election) {

        voterRepository.deleteAll();
        voterSeedDataService.votesFromMap(candidates, election);
        voterRepository.save(voterSeedDataService.getVotes());
    }
}
