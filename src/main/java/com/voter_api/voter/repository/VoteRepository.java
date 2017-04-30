package com.voter_api.voter.repository;

import com.voter_api.voter.domain.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VoteRepository extends MongoRepository<Vote, String> {

}
