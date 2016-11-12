package com.example.voter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VoteCount {

    private String vote;

    private int count;

    public VoteCount() {
    }

    public VoteCount(String vote, int count) {
        this.vote = vote;
        this.count = count;
    }

    public String getVote() {
        return vote;
    }

    public int getCount() {
        return count;
    }
}
