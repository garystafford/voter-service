package com.example.voter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CandidateList {

    private List<String> candidates = Arrays.asList(
          "Donald Trump",
          "Chris Keniston",
          "Jill Stein",
          "Gary Johnson",
          "Darrell Castle",
          "Hillary Clinton"
  );

  public List<String> getCandidates() {
      List<String> candidatesSorted = candidates.subList(0, candidates.size());
      Collections.sort(candidatesSorted);
      return candidatesSorted;
  }

    @Autowired
    RestTemplate restTemplate;


    public ResponseEntity<List<String>> getCandidatesRemote() {

        String results = restTemplate.getForObject("http://candidate/", String.class);

        ParameterizedTypeReference<List<String>> typeRef =
              new ParameterizedTypeReference<List<String>>() {
              };

      ResponseEntity<List<String>> responseEntity =
              restTemplate.exchange("http://candidate/candidates/summary", HttpMethod.GET, null, typeRef);

      return responseEntity;
//      List<Candidate> candidatesSorted = candidates.subList(0, candidates.size());
//      Collections.sort(candidatesSorted);
//      return candidatesSorted;
  }
}
