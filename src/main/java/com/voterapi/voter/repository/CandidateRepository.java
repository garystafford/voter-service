package com.voterapi.voter.repository;

import com.voterapi.voter.domain.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CandidateRepository extends MongoRepository<Candidate, String> {
    List<Candidate> findByElectionContains(@Param("election") String election);
}
