package com.voter_api.voter.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Vote {

    @Id
    private String id;

    @JsonProperty("candidate")
    private String vote;

    private String election;

    Vote() {
        //empty constructor
    }

    public Vote(String vote, String election) {
        this.vote = vote;
        this.election = election;
    }

    public String getId() {
        return id;
    }

    public String getVote() {
        return vote;
    }

    public String getElection() {
        return election;
    }

    @Override
    public String toString() {
        return String.format("Vote[id=%s, name='%s']", id, getVote());
    }
}
