package com.voterapi.voter.controller;

import com.voterapi.voter.domain.CandidateVoterView;
import com.voterapi.voter.domain.Vote;
import com.voterapi.voter.domain.VoteCount;
import com.voterapi.voter.domain.VoteCountWinner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VoterControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VoterController voterController;

    @Autowired
    private Environment environment;

    @Before
    public void setup() {
        createSampleVoterResults();
        createSampleCandidateList();
    }

    private void createSampleVoterResults() {
        // create sample voter test data
        String election = "2016 Presidential Election";
        Map<String, String> candidates = new HashMap<>();
        candidates.put("Chris Keniston", "3");
        candidates.put("Darrell Castle", "2");
        candidates.put("Donald Trump", "8");
        candidates.put("Gary Johnson", "3");
        candidates.put("Hillary Clinton", "14");
        candidates.put("Jill Stein", "5");
        voterController.getSimulation(candidates, election);
    }

    private void createSampleCandidateList() {
        // create sample list of candidates by calling the candidate service
        String election = "2016 Presidential Election";
        String candidateServiceHostname = environment.getProperty("services.candidate.host");
        String candidateServicePort = environment.getProperty("services.candidate.port");
        String candidateContextPath = environment.getProperty("services.candidate.context-path");
        String candidateServiceResourceUrl = String.format("http://%s:%s/%s/candidates/simulation",
                candidateServiceHostname, candidateServicePort, candidateContextPath, election);
        restTemplate.getForEntity(candidateServiceResourceUrl, String.class);

        candidateServiceResourceUrl = String.format("http://%s:%s/%s/candidates/summary/election/%s",
                candidateServiceHostname, candidateServicePort, candidateContextPath, election);
        restTemplate.getForEntity(candidateServiceResourceUrl, String.class);
    }

    //    @Ignore("Broken Test - Need to Fix")
    @Test
    public void getCandidatesHttpReturnsListOfCandidateChoices() throws Exception {
        String election = "2016 Presidential Election";
        ParameterizedTypeReference<Map<String, List<CandidateVoterView>>> typeRef =
                new ParameterizedTypeReference<Map<String, List<CandidateVoterView>>>() {
                };
        ResponseEntity<Map<String, List<CandidateVoterView>>> responseEntity =
                restTemplate.exchange(String.format("/candidates/http/%s", election),
                        HttpMethod.GET, null, typeRef);
        assertThat(responseEntity.getStatusCode().value() == 200).isTrue();
        assertThat(responseEntity.getBody().containsKey("candidates")).isTrue();
    }

    @Test
    public void postVoteReturnsNewVote() throws Exception {
        String expectedVote = "Test Vote";
        String expectedElection = "Test Election";
        Vote vote = new Vote(expectedVote, expectedElection);
        ResponseEntity<Vote> responseEntity =
                restTemplate.postForEntity("/votes", vote, Vote.class);
        assertThat(responseEntity.getStatusCode().value() == 201).isTrue();
        assertThat(responseEntity.getBody().getCandidate()).isEqualTo(expectedVote);
    }

    @Test
    public void getResultsReturnsListOfExpectedVoteCounts() throws Exception {
        String election = "2016 Presidential Election";
        String expectedVote = "Hillary Clinton";
        int expectedCount = 14;
        ParameterizedTypeReference<Map<String, List<VoteCount>>> typeRef =
                new ParameterizedTypeReference<Map<String, List<VoteCount>>>() {
                };
        ResponseEntity<Map<String, List<VoteCount>>> responseEntity =
                restTemplate.exchange(String.format("/results/%s", election), HttpMethod.GET, null, typeRef);
        LinkedHashMap body = ((LinkedHashMap) responseEntity.getBody());
        Collection voteCountCollection = body.values();
        ArrayList voteCountArray = (ArrayList) voteCountCollection.toArray()[0];
        VoteCount voteCount = (VoteCount) voteCountArray.get(0);
        assertThat(responseEntity.getStatusCode().value() == 200).isTrue();
        assertThat(voteCount.getCandidate()).isEqualTo(expectedVote);
        assertThat(voteCount.getVotes()).isEqualTo(expectedCount);
    }

    @Test
    public void getTotalVotesReturnsSumOfVotes() throws Exception {
        String election = "2016 Presidential Election";
        int expectedCount = 35;
        ResponseEntity<VoteCountWinner> responseEntity =
                restTemplate.getForEntity(String.format("/results/%s/votes", election), VoteCountWinner.class);
        VoteCountWinner voteCount = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode().value() == 200).isTrue();
        assertThat(voteCount.getVotes()).isEqualTo(expectedCount);
    }

    @Test
    public void getWinnersReturnsCandidatesWithMostVotes() throws Exception {
        String election = "2016 Presidential Election";
        String expectedVote = "Hillary Clinton";
        int expectedCount = 14;
        ParameterizedTypeReference<Map<String, List<VoteCount>>> typeRef =
                new ParameterizedTypeReference<Map<String, List<VoteCount>>>() {
                };
        ResponseEntity<Map<String, List<VoteCount>>> responseEntity =
                restTemplate.exchange(String.format("/winners/%s", election), HttpMethod.GET, null, typeRef);
        LinkedHashMap body = ((LinkedHashMap) responseEntity.getBody());
        Collection voteCountCollection = body.values();
        ArrayList voteCountArray = (ArrayList) voteCountCollection.toArray()[0];
        VoteCount voteCount = (VoteCount) voteCountArray.get(0);
        assertThat(responseEntity.getStatusCode().value() == 200).isTrue();
        assertThat(voteCount.getCandidate()).isEqualTo(expectedVote);
        assertThat(voteCount.getVotes()).isEqualTo(expectedCount);
    }

    @Test
    public void getWinnersVotesReturnsWinnersVoteCount() throws Exception {
        String election = "2016 Presidential Election";
        int expectedCount = 14;
        ResponseEntity<VoteCountWinner> responseEntity =
                restTemplate.getForEntity(String.format("/winners/%s/votes", election), VoteCountWinner.class);
        VoteCountWinner voteCountWinner = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode().value() == 200).isTrue();
        assertThat(voteCountWinner.getVotes()).isEqualTo(expectedCount);
    }

    @Test
    public void getSimulationReturnsExpectedMessage() throws Exception {
        String election = "2012 Presidential Election";
        String expectedResponse =
                "{\"message\":\"Simulation data created!\"}";
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(String.format("/simulation/http/%s", election), String.class);
        assertThat(responseEntity.getStatusCode().value() == 200).isTrue();
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    public void deleteAllVotesReturnsExpectedStatusCode() throws Exception {
        ResponseEntity<Void> responseEntity =
                restTemplate.postForEntity("/votes/drop", null, Void.class);
        assertThat(responseEntity.getStatusCode().value() == 204).isTrue();
        assertThat(responseEntity.getBody()).isEqualTo(null);
    }

    @Test
    public void deleteAllCandidatesReturnsExpectedStatusCode() throws Exception {
        ResponseEntity<Void> responseEntity =
                restTemplate.postForEntity("/candidates/drop", null, Void.class);
        assertThat(responseEntity.getStatusCode().value() == 204).isTrue();
        assertThat(responseEntity.getBody()).isEqualTo(null);
    }
}
