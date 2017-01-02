package com.example.voter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VoteCountWinner {

    @JsonProperty("votes")
    private int count;

    public VoteCountWinner() {
    }

    public VoteCountWinner(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
