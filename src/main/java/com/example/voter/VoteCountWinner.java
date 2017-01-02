package com.example.voter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VoteCountWinner {

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
