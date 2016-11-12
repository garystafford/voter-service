package com.example.voter;

import java.util.Arrays;
import java.util.List;

public class VoteSeedData {

    static List<Vote> votes = Arrays.asList(
            new Vote("Hillary Clinton"),
            new Vote("Donald Trump"),
            new Vote("Chris Keniston"),
            new Vote("Jill Stein"),
            new Vote("Gary Johnson"),
            new Vote("Darrell Castle"),
            new Vote("Hillary Clinton"),
            new Vote("Hillary Clinton"),
            new Vote("Hillary Clinton"),
            new Vote("Hillary Clinton"),
            new Vote("Hillary Clinton"),
            new Vote("Hillary Clinton"),
            new Vote("Hillary Clinton"),
            new Vote("Hillary Clinton"),
            new Vote("Hillary Clinton"),
            new Vote("Hillary Clinton"),
            new Vote("Hillary Clinton"),
            new Vote("Hillary Clinton"),
            new Vote("Donald Trump"),
            new Vote("Donald Trump"),
            new Vote("Donald Trump"),
            new Vote("Donald Trump"),
            new Vote("Donald Trump"),
            new Vote("Donald Trump"),
            new Vote("Donald Trump"),
            new Vote("Chris Keniston"),
            new Vote("Chris Keniston"),
            new Vote("Jill Stein"),
            new Vote("Jill Stein"),
            new Vote("Jill Stein"),
            new Vote("Jill Stein"),
            new Vote("Gary Johnson"),
            new Vote("Gary Johnson"),
            new Vote("Gary Johnson"),
            new Vote("Darrell Castle")
    );

    static List<Vote> getVotes() {
        return votes;
    }
}
