package com.voterapi.voter.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Candidate {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String politicalParty;
    private String election;

    public Candidate() {
        // unused constructor
    }

    public Candidate(String firstName, String lastName, String politicalParty, String election) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.politicalParty = politicalParty;
        this.election = election;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getPoliticalParty() {
        return politicalParty;
    }

    public String getElection() {
        return election;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getFullName(), politicalParty);
    }
}
