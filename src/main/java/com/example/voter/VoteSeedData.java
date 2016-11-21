package com.example.voter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class VoteSeedData {

    private List<Vote> votes = new ArrayList<>();

    public VoteSeedData() {
    }

    public List<Vote> getVotes() {
        return votes;
    }

    // accepts map of candidates and total candidates
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
        Map candidates = new HashMap();
        List<String> candidateList = CandidateList.candidates;
        for (int i = 0; i < candidateList.size(); i++) {
            candidates.put(candidateList.get(i), getRandomIntAsString(5, 15));
        }
        votesFromMap(candidates);
    }

    // returns random number as string
    private String getRandomIntAsString(int min, int max) {
        int randomVoteCount = ThreadLocalRandom.current().nextInt(min, max + 1);
        return Integer.toString(randomVoteCount);
    }
}
