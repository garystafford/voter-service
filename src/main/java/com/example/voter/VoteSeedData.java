package com.example.voter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class VoteSeedData {

    private List<Vote> votes = new ArrayList<>();

    public VoteSeedData() {
    }

    public List<Vote> getVotes() {
        return votes;
    }

    // accepts map of candidates and total votes
    public void votesFromMap(Map candidates) {
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
        Map candidatesMap = new HashMap();
        CandidateList candidateList = new CandidateList();
        List<String> candidates = candidateList.getCandidates();
        for (int i = 0; i < candidates.size(); i++) {
            candidatesMap.put(candidates.get(i), getRandomIntAsString(2, 20));
        }
        votesFromMap(candidatesMap);
    }

    // returns random number as string
    private String getRandomIntAsString(int min, int max) {
        int randomVoteCount = ThreadLocalRandom.current().nextInt(min, max + 1);
        return Integer.toString(randomVoteCount);
    }
}
