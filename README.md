[![Build Status](https://travis-ci.org/garystafford/voter-service.svg?branch=rabbitmq)](https://travis-ci.org/garystafford/voter-service) [![Dependencies](https://app.updateimpact.com/badge/817200262778327040/voter-service.svg?config=compile)](https://app.updateimpact.com/latest/817200262778327040/voter-service) [![Layers](https://images.microbadger.com/badges/image/garystafford/voter-service.svg)](https://microbadger.com/images/garystafford/voter-service "Get your own image badge on microbadger.com") [![Version](https://images.microbadger.com/badges/version/garystafford/voter-service.svg)](https://microbadger.com/images/garystafford/voter-service "Get your own version badge on microbadger.com")

# Voter Service

## Introduction

The Voter [Spring Boot](https://projects.spring.io/spring-boot/) Service is a RESTful Web Service, backed by [MongoDB](https://www.mongodb.com/). The Voter service exposes several HTTP API endpoints, listed below. API users can review a list candidates, submit a candidate, view voting results, and inspect technical information about the running service. API users can also create random voting data by calling the `/voter/simulation` endpoint.

The Voter service is designed to work along with the [Candidate Service](https://github.com/garystafford/candidate-service), as part of a complete API. The Voter service is dependent on the Candidate service to supply a list of candidates. The Candidate service is called by the Voter service, using one of two methods:
1. [HTTP-based Synchronous IPC](https://www.nginx.com/blog/building-microservices-inter-process-communication/), when either the Voter service's `/voter/candidates/election/{election}` or `/voter/simulation/election/{election}` endpoints are called.
2. [Messaging-based Remote Procedure Call (RPC) IPC](http://www.rabbitmq.com/tutorials/tutorial-six-java.html), when either the Voter service's `/voter/candidates/rpc/election/{election}` or `/voter/simulation/rpc/election/{election}` endpoints are called.

## Quick Start for Local Development

The Voter service requires MongoDB to be running locally, on port `27017`, RabbitMQ running on `5672` and `15672`, and the Candidate service to be running on `8097`. To clone, build, test, and run the Voter service as a JAR file, locally:

```bash
git clone --depth 1 --branch rabbitmq \
  https://github.com/garystafford/voter-service.git
cd voter-service
./gradlew clean cleanTest build
java -jar build/libs/voter-service-0.3.0.jar
```
## Quick Start with Docker
The easiest way to run the Voter API services locally is using the `docker-compose-local.yml` file, included in the project. The Docker Compose file will spin up single container instances of the Voter service, Candidate service, RabbitMQ, and MongoDB.

```bash
sh ./stack-deploy-local.sh
```

```text
COMMAND                  CREATED             STATUS              PORTS                                                                                        NAMES
6232727faffd        garystafford/voter-api-gateway:latest     "/docker-entrypoin..."   39 seconds ago      Up 38 seconds       0.0.0.0:8080->8080/tcp                                                                       voterstack_voter-api-gateway_1
97444d209416        garystafford/voter-service:rabbitmq       "java -Dspring.pro..."   40 seconds ago      Up 39 seconds       0.0.0.0:8099->8099/tcp                                                                       voterstack_voter_1
f78ab6fb491b        garystafford/candidate-service:rabbitmq   "java -Dspring.pro..."   40 seconds ago      Up 39 seconds       0.0.0.0:8097->8097/tcp                                                                       voterstack_candidate_1
0f851d9d0e4c        rabbitmq:management-alpine                "docker-entrypoint..."   41 seconds ago      Up 40 seconds       4369/tcp, 5671/tcp, 0.0.0.0:5672->5672/tcp, 15671/tcp, 25672/tcp, 0.0.0.0:15672->15672/tcp   voterstack_rabbitmq_1
8238959426a1        mongo:latest                              "docker-entrypoint..."   41 seconds ago      Up 40 seconds       0.0.0.0:27017->27017/tcp                                                                     voterstack_mongodb_1
```

## Getting Started with the API
The easiest way to get started with the Candidate and Voter services API, using [HTTPie](https://httpie.org/) from the command line:  
1. Create sample candidates: `http http://localhost:8097/candidate/simulation`  
2. View sample candidates: `http http://localhost:8097/candidate/candidates/summary/election/2016%20Presidential%20Election`  
3. Create sample voter data: `http http://localhost:8099/voter/simulation/election/2016%20Presidential%20Election`  
4. View sample voter results: `http http://localhost:8099/voter/results`

Alternately, for step 3 above, you can use Service-to-Service RPC IPC with RabbitMQ, to retrieve the candidates:  
`http http://localhost:8099/voter/simulation/rpc/election/2016%20Presidential%20Election`

## Voter Service Endpoints

The service uses a context path of `/voter`. All endpoints must be are prefixed with this sub-path.

Purpose                                                                                                                  | Method  | Endpoint
------------------------------------------------------------------------------------------------------------------------ | :------ | :-----------------------------------------------------
Create Random Sample Data                                                                                                | GET     | [/voter/simulation/election/{election}](http://localhost:8099/voter/simulation/election/{election})
Create Random Sample Data (using RPC Messaging)                                                                          | GET     | [/voter/simulation/rpc/election/{election}](http://localhost:8099/voter/simulation/rpc/election/{election})
List Candidates                                                                                                          | GET     | [/voter/candidates/election/{election}](http://localhost:8099/voter/candidates/election/{election})
List Candidates (using RPC Messaging)                                                                                    | GET     | [/voter/candidates/rpc/election/{election}](http://localhost:8099/voter/candidates/rpc/election/{election})
Submit Vote                                                                                                              | POST    | [/voter/votes](http://localhost:8099/voter/votes)
View Voting Results                                                                                                      | GET     | [/voter/results](http://localhost:8099/voter/results)
View Total Votes                                                                                                         | GET     | [/voter/results/votes](http://localhost:8099/voter/results/votes)
View Winner(s)                                                                                                           | GET     | [/voter/winners](http://localhost:8099/voter/winners)
View Winning Vote Count                                                                                                  | GET     | [/voter/winners/votes](http://localhost:8099/voter/winners/votes)
Service Info                                                                                                             | GET     | [/voter/info](http://localhost:8099/voter/info)
Service Health                                                                                                           | GET     | [/voter/health](http://localhost:8099/voter/health)
Service Metrics                                                                                                          | GET     | [/voter/metrics](http://localhost:8099/voter/metrics)
Other [Spring Actuator](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) endpoints | GET     | `/actuator`, `/mappings`, `/env`, `/configprops`, etc.
Other [HATEOAS](https://spring.io/guides/gs/rest-hateoas) endpoints for `/voter/votes`                                         | Various | DELETE, PATCH, PUT, page sort, size, etc.

The [HAL Browser](https://github.com/mikekelly/hal-browser) API browser for the `hal+json` media type is installed alongside the service. It can be accessed at `http://localhost:8099/voter/actuator/`.

## Voting

Submitting a new candidate requires an HTTP `POST` request to the `/voter/votes` endpoint, as follows:

HTTPie

```text
http POST http://localhost:8099/voter/votes \
  candidate="Jill Stein" \
  election="2016 Presidential Election"
```

cURL

```text
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{ "candidate": "Jill Stein", "election": "2016 Presidential Election" }' \
  "http://localhost:8099/voter/votes"
```

wget

```text
wget --method POST \
  --header 'content-type: application/json' \
  --body-data '{ "candidate": "Jill Stein", "election": "2016 Presidential Election" }' \
  --no-verbose \
  --output-document - http://localhost:8099/voter/votes
```

## Sample Output

Using [HTTPie](https://httpie.org/) command line HTTP client.

`http http://localhost:8099/voter/simulation/election/2016%20Presidential%20Election`
or  
`http http://localhost:8099/voter/simulation/rpc/election/2016%20Presidential%20Election`

```json
{
    "message": "Simulation data created!"
}
```

`http http://localhost:8099/voter/candidates/election/2016%20Presidential%20Election`
or  
`http http://localhost:8099/voter/candidates/rpc/election/2016%20Presidential%20Election`

```json
{
    "candidates": [
        {
            "election": "2016 Presidential Election",
            "fullName": "Darrell Castle",
            "politicalParty": "Constitution Party"
        },
        {
            "election": "2016 Presidential Election",
            "fullName": "Hillary Clinton",
            "politicalParty": "Democratic Party"
        },
        {
            "election": "2016 Presidential Election",
            "fullName": "Gary Johnson",
            "politicalParty": "Libertarian Party"
        }
    ]
}
```

`http http://localhost:8099/voter/results`

```json
{
    "results": [
        {
            "candidate": "Darrell Castle",
            "votes": 19
        },
        {
            "candidate": "Donald Trump",
            "votes": 15
        },
        {
            "candidate": "Gary Johnson",
            "votes": 15
        },
        {
            "candidate": "Jill Stein",
            "votes": 13
        }
    ]
}
```

`http http://localhost:8099/voter/results/votes`

```json
{
    "votes": 80
}
```

`http http://localhost:8099/voter/winners`

```json
{
    "results": [
        {
            "candidate": "Darrell Castle",
            "votes": 19
        }
    ]
}
```

`http http://localhost:8099/voter/winners/votes`

```json
{
    "votes": 19
}
```

`http POST http://localhost:8099/voter/votes \
    candidate="Jill Stein" \
    election="2016 Presidential Election"`

```json
{
    "_links": {
        "self": {
            "href": "http://localhost:8099/voter/votes/590548541b8ebf700f9c2a62"
        },
        "candidate": {
            "href": "http://localhost:8099/voter/votes/590548541b8ebf700f9c2a62"
        }
    },
    "candidate": "Jill Stein",
    "election": "2016 Presidential Election"
}
```

## Continuous Integration

The project's source code is continuously built and tested on every commit to [GitHub](https://github.com/garystafford/voter-service), using [Travis CI](https://travis-ci.org/garystafford/voter-service). If all unit tests pass, the resulting Spring Boot JAR is pushed to the `build-artifacts` branch of the [voter-service](https://github.com/garystafford/voter-service/tree/build-artifacts) GitHub repository. The JAR's filename is incremented with each successful build (i.e. `voter-service-0.2.10.jar`).

![Vote Continuous Integration Pipeline](Voter-CI.png)

## Spring Profiles

The Voter service includes several Spring Boot Profiles, in a multi-profile YAML document: `src/main/resources/application.yml`. The profiles are `default`, `docker-development`, `docker-production`, and `aws-production`. You will need to ensure your MongoDB instance is available at that `host` address and port of the profile you choose, or you may override the profile's properties.


```yaml
endpoints:
  enabled: true
  sensitive: false
info:
  java:
    source: "${java.version}"
logging:
  level:
    root: INFO
management:
  info:
    build:
      enabled: true
    git:
      mode: full
server:
  port: 8099
  context-path: /voter
services:
  candidate:
    host: localhost
    port: 8097
    context-path: candidate
spring:
  data:
    mongodb:
      database: voters
      host: localhost
      port: 27017
  rabbitmq:
    host: localhost
---
services:
  candidate:
    host: candidate
spring:
  data:
    mongodb:
      host: mongodb
  rabbitmq:
    host: rabbitmq
  profiles: docker-local
---
endpoints:
  candidate:
    host: candidate
  enabled: false
  health:
    enabled: true
  info:
    enabled: true
  sensitive: true
  services: ~
logging:
  level:
    root: WARN
management:
  info:
    build:
      enabled: false
    git:
      enabled: false
spring:
  data:
    mongodb:
      host: "10.0.1.6"
  rabbitmq:
    host: "10.0.1.8"
  profiles: aws-production
---
endpoints:
  enabled: false
  health:
    enabled: true
  info:
    enabled: true
  sensitive: true
logging:
  level:
    root: WARN
management:
  info:
    build:
      enabled: false
    git:
      enabled: false
services:
  candidate:
    host: candidate
spring:
  data:
    mongodb:
      host: mongodb
  rabbitmq:
    host: rabbitmq
  profiles: docker-production
```

All profile property values may be overridden on the command line, or in a `.conf` file. For example, to start the Voter service with the `aws-production` profile, but override the `mongodb.host` value with a new host address, you might use the following command:

```bash
java -jar <name_of_jar_file> \
  --spring.profiles.active=aws-production \
  --spring.data.mongodb.host=<new_host_address>
  -Dlogging.level.root=DEBUG \
  -Djava.security.egd=file:/dev/./urandom
```

## References

- [Spring Data MongoDB - Reference Documentation](http://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [Accessing MongoDB Data with REST](https://spring.io/guides/gs/accessing-mongodb-data-rest/)
- [Spring Boot Testing](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing)
- [Installing Spring Boot applications](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-install)
- [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
- [2016 Presidential Candidates](http://www.politics1.com/p2016.htm)
