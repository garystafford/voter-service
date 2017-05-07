package com.voterapi.voter.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VoteCount {

    private String candidate;

    private int votes;

    public VoteCount() {
        //empty constructor
    }

    public VoteCount(String candidate, int votes) {
        this.candidate = candidate;
        this.votes = votes;
    }

    public String getCandidate() {
        return candidate;
    }

    public int getVotes() {
        return votes;
    }
}
