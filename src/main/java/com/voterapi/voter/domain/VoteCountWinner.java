package com.voterapi.voter.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VoteCountWinner {

    private int votes;

    public VoteCountWinner() {
        //empty constructor
    }

    public VoteCountWinner(int votes) {
        this.votes = votes;
    }

    public int getVotes() {
        return votes;
    }
}
