package com.example.voter;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;


public interface VoteRepository extends MongoRepository<Vote, String> {

}
