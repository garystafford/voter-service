package com.voterapi.voter.repository;

import com.voterapi.voter.domain.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VoterRepository extends MongoRepository<Vote, String> {

}
