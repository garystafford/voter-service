package com.example.voter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@RestController
public class VoteController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private VoteRepository voteRepository;

    @RequestMapping(value = "/choices", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<String>>> getChoices() {
        List<String> results = VoteList.getVotes();
        return new ResponseEntity<>(Collections.singletonMap("choices", results), HttpStatus.OK);
    }

    @RequestMapping(value = "/results", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<VoteCount>>> getResults() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("vote").count().as("count"),
                project("count").and("vote").previousOperation(),
                sort(Sort.Direction.ASC, "vote")
        );

        AggregationResults<VoteCount> groupResults
                = mongoTemplate.aggregate(aggregation, Vote.class, VoteCount.class);
        List<VoteCount> results = groupResults.getMappedResults();
        return new ResponseEntity<>(Collections.singletonMap("results", results), HttpStatus.OK);
    }

    @RequestMapping(value = "/favorite", method = RequestMethod.GET)
    public ResponseEntity<VoteCount> getFavorite() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("vote").count().as("count"),
                project("count").and("vote").previousOperation(),
                sort(Sort.Direction.DESC, "count"),
                limit(1)
        );

        AggregationResults<VoteCount> groupResults =
                mongoTemplate.aggregate(aggregation, Vote.class, VoteCount.class);
        VoteCount result = groupResults.getMappedResults().get(0);

        return ResponseEntity.status(HttpStatus.OK).body(result); // return 200 with payload
    }

    @RequestMapping(value = "/seeder", method = RequestMethod.GET)
    public ResponseEntity<List<Vote>> seedSampleData() {

        voteRepository.deleteAll();
        List<Vote> voteSeedData = VoteSeedData.getVotes();
        voteRepository.save(voteSeedData);
        return ResponseEntity.status(HttpStatus.OK).body(null); // return 200 without payload
    }
}
