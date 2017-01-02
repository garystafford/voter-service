package com.example.voter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class Vote {

    @Id
    private String id;

    @JsonProperty("candidate")
    private String vote;

    Vote() {
        // unused constructor
    }

    Vote(String vote) {
        this.vote = vote;
    }

    public String getId() {
        return id;
    }

    public String getVote() {
        return vote;
    }

    @Override
    public String toString() {
        return String.format("Vote[id=%s, name='%s']", id, getVote());
    }
}
