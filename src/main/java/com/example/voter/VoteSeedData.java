package com.example.voter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class VoteSeedData {

    private CandidateList candidateList;

    private List<Vote> votes = new ArrayList<>();

    @Autowired
    public VoteSeedData(CandidateList candidateList) {
        this.candidateList = candidateList;
    }

    List<Vote> getVotes() {
        return votes;
    }

    // accepts map of candidates and total votes
    void votesFromMap(Map candidates) {
        votes.clear(); // clear previous seed data from list
        for (Object key : candidates.keySet()) {
            int value = Integer.parseInt(String.valueOf(candidates.get(key)));
            for (int i = 0; i < value; i++) {
                String candidate = String.valueOf(key);
                votes.add(new Vote(candidate));
            }
        }
    }

    // generates random number of total votes for each candidate
    void setRandomVotesHttp() {
        Map candidates = new HashMap();
        List<String> list = candidateList.getCandidatesSyncHttp();
        for (String aList : list) {
            candidates.put(aList, getRandomIntAsString(2, 20));
        }
        votesFromMap(candidates);
    }

    // generates random number of total votes for each candidate
    void setRandomVotesRpc() {
        Map candidates = new HashMap();
        List<String> list = candidateList.getCandidatesMessageRpc();
        for (String aList : list) {
            candidates.put(aList, getRandomIntAsString(2, 20));
        }
        votesFromMap(candidates);
    }

    // returns random number as string
    private String getRandomIntAsString(int min, int max) {
        int randomVoteCount = ThreadLocalRandom.current().nextInt(min, max + 1);
        return Integer.toString(randomVoteCount);
    }
}
