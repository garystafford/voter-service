package com.voterapi.voter.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Vote {

    @Id
    private String id;

    private String candidate;

    private String election;

    Vote() {
        //empty constructor
    }

    public Vote(String candidate, String election) {
        this.candidate = candidate;
        this.election = election;
    }

    public String getId() {
        return id;
    }

    public String getCandidate() {
        return candidate;
    }

    public String getElection() {
        return election;
    }

    @Override
    public String toString() {
        return String.format("Vote[id=%s, name='%s']", id, getCandidate());
    }
}
