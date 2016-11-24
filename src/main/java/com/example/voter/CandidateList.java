package com.example.voter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CandidateList {

    private static List<String> candidates = Arrays.asList(
            "Donald Trump",
            "Chris Keniston",
            "Jill Stein",
            "Gary Johnson",
            "Darrell Castle",
            "Hillary Clinton"
    );

    public static List<String> getCandidates() {
        List<String> candidatesSorted = candidates.subList(0, candidates.size());
        Collections.sort(candidatesSorted);
        return candidatesSorted;
    }
}
