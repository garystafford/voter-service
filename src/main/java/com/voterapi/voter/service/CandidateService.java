package com.voterapi.voter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import com.voterapi.voter.domain.Candidate;
import com.voterapi.voter.domain.CandidateVoterView;
import com.voterapi.voter.repository.CandidateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@Component
public class CandidateService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private MongoTemplate mongoTemplate;
    private CandidateRepository candidateRepository;
    private Environment environment;

    @Autowired
    public CandidateService(MongoTemplate mongoTemplate,
                            CandidateRepository candidateRepository,
                            Environment environment) {
        this.mongoTemplate = mongoTemplate;
        this.candidateRepository = candidateRepository;
        this.environment = environment;

        getAzureServiceBusCandidateQueueMessages();
    }

    /**
     * Retrieves candidates from MongoDB and transforms to voter view
     *
     * @param election
     * @return List of candidates
     */
    public List<CandidateVoterView> getCandidatesQueueDb(String election) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("election").is(election)),
                project("firstName", "lastName", "politicalParty", "election")
                        .andExpression("concat(firstName,' ', lastName)")
                        .as("fullName"),
                sort(Sort.Direction.ASC, "lastName")
        );

        AggregationResults<CandidateVoterView> groupResults
                = mongoTemplate.aggregate(aggregation, Candidate.class, CandidateVoterView.class);

        return groupResults.getMappedResults();
    }

    public void getAzureServiceBusCandidateQueueMessages() {
        String connectionString = environment.getProperty("azure.service-bus.connection-string");
        String queueName = "candidates.queue";

        try {
            IQueueClient queueReceiveClient = new QueueClient(
                    new ConnectionStringBuilder(connectionString, queueName), ReceiveMode.PEEKLOCK);

            queueReceiveClient.registerMessageHandler(new MessageHandler(queueReceiveClient, candidateRepository),
                    new MessageHandlerOptions(1, false, Duration.ofMinutes(1)));
        } catch (InterruptedException | ServiceBusException e) {
            logger.info(String.valueOf(e.getStackTrace()));
        }
    }

    static class MessageHandler implements IMessageHandler {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        private CandidateRepository candidateRepository;
        private IQueueClient client;

        public MessageHandler(IQueueClient client, CandidateRepository candidateRepository) {
            this.client = client;
            this.candidateRepository = candidateRepository;
        }

        @Override
        public CompletableFuture<Void> onMessageAsync(IMessage iMessage) {
            logger.info(String.format("Received message with sq#: %d and lock token: %s.",
                    iMessage.getSequenceNumber(), iMessage.getLockToken()));
            return this.client.completeAsync(iMessage.getLockToken()).thenRunAsync(() ->
                    createCandidateFromMessage(new String(iMessage.getBody()))
            );
        }

        @Override
        public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
            logger.info(String.format("%s-%s", exceptionPhase, throwable.getMessage()));
        }

        private void createCandidateFromMessage(String candidateMessage) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            TypeReference<Candidate> mapType = new TypeReference<Candidate>() {
            };

            Candidate candidate = null;

            try {
                candidate = objectMapper.readValue(candidateMessage, mapType);
            } catch (IOException e) {
                logger.info(String.valueOf(e));
            }

            candidateRepository.save(candidate);

            if (logger.isDebugEnabled()) {
                logger.debug("Candidate {} saved to MongoDB", candidate.toString());
            }
        }

    }
}