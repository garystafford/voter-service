package com.voterapi.voter.domain;

public class CandidateVoterView {

    private String fullName;
    private String politicalParty;
    private String election;

    CandidateVoterView() {
        // unused constructor
    }

    public CandidateVoterView(String fullName, String politicalParty, String election) {
        this.fullName = fullName;
        this.politicalParty = politicalParty;
        this.election = election;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPoliticalParty() {
        return politicalParty;
    }

    public String getElection() {
        return election;
    }
}
