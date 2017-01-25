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

    @Autowired
    private CandidateList candidateList;

    private List<Vote> votes = new ArrayList<>();

    public VoteSeedData() {
    }

    public List<Vote> getVotes() {
        return votes;
    }

    // accepts map of candidates and total votes
    public void votesFromMap(Map candidates) {
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
    public void setRandomVotes() {
        Map candidates = new HashMap();
        List<String> list = candidateList.getCandidates();
        for (int i = 0; i < list.size(); i++) {
            candidates.put(list.get(i), getRandomIntAsString(2, 20));
        }
        votesFromMap(candidates);
    }

    // returns random number as string
    private String getRandomIntAsString(int min, int max) {
        int randomVoteCount = ThreadLocalRandom.current().nextInt(min, max + 1);
        return Integer.toString(randomVoteCount);
    }
}
