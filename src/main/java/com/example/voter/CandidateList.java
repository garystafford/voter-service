package com.example.voter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class CandidateList {

    static List<String> candidates = Arrays.asList(
            "Hillary Clinton",
            "Donald Trump",
            "Chris Keniston",
            "Jill Stein",
            "Gary Johnson",
            "Darrell Castle",
            "Hillary Clinton"
    );

    static List<String> getCandidates() {
        List<String> candidatesSorted = candidates.subList(1, candidates.size());
        Collections.sort(candidatesSorted);
        return candidatesSorted;
    }
}
