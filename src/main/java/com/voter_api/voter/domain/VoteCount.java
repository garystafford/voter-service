package com.voter_api.voter.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VoteCount {

    @JsonProperty("candidate")
    private String vote;

    @JsonProperty("votes")
    private int count;

    public VoteCount() {
        //empty constructor
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
