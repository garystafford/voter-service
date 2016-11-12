package com.example.voter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class VoteList {

    static List<String> votes = Arrays.asList(
      "Hillary Clinton",
      "Donald Trump",
      "Chris Keniston",
      "Jill Stein",
      "Gary Johnson",
      "Darrell Castle"
    );

    static List<String> getVotes() {
        List<String> voteSorted = votes.subList(1, votes.size());
        Collections.sort(voteSorted);
        return voteSorted;
    }
}
