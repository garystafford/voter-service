[![Build Status](https://travis-ci.org/garystafford/voter-service.svg?branch=master)](https://travis-ci.org/garystafford/voter-service)

# Voter Service

## Introduction

A simple Spring Boot RESTful microservice, backed by MongoDB.

## Quick Start

To clone, build, test, and run the Voter Spring Boot service locally, requires MongoDB to be pre-installed and running on port `27017`.

```bash
git clone https://github.com/garystafford/voter-service.git
cd voter-service
./gradlew clean cleanTest build
java -jar build/libs/voter-service-0.1.0.jar
```

## Primary Service Endpoints

Out of the box, the service runs on `localhost`, port `8099`. By default, the service looks for MongoDB on `localhost`, port `27017`.

- Purge and Add New Sample Data (GET): <http://localhost:8099/seeder>
- List Candidates (GET): <http://localhost:8099/choices>
- Submit Vote (POST): <http://localhost:8099/votes>
- View Voting Results (GET): <http://localhost:8099/results>
- View Winner (GET): <http://localhost:8099/winner>
- Service Health (GET): <http://localhost:8099/health>
- Service Metrics (GET): <http://localhost:8099/metrics>
- Other [Spring Actuator](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) endpoints include: `/mappings`, `/env`, `/configprops`, etc.
- Other [HATEOAS](https://spring.io/guides/gs/rest-hateoas) endpoints for `/votes` include: DELETE, PATCH, PUT, page sort, size, etc.

## How to POST a Vote:

HTTPie

```text
http POST http://localhost:8099/votes vote="Hillary Clinton"
```

cURL

```text
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{ "vote": "Hillary Clinton" }' \
  "http://localhost:8099/votes"
```

wget

```text
wget --method POST \
  --header 'content-type: application/json' \
  --body-data '{ "vote": "Hillary Clinton" }' \
  --no-verbose \
  --output-document - http://localhost:8099/votes
```

## Sample Output

`http://localhost:8099/choices`

```json
{
    "choices": [
        "Chris Keniston",
        "Darrell Castle",
        "Donald Trump",
        "Gary Johnson",
        "Hillary Clinton",
        "Jill Stein"
    ]
}
```

`http://localhost:8099/results`

```json
{
    "results": [
        {
            "count": 3,
            "vote": "Chris Keniston"
        },
        {
            "count": 2,
            "vote": "Darrell Castle"
        },
        {
            "count": 8,
            "vote": "Donald Trump"
        },
        {
            "count": 4,
            "vote": "Gary Johnson"
        },
        {
            "count": 14,
            "vote": "Hillary Clinton"
        },
        {
            "count": 5,
            "vote": "Jill Stein"
        }
    ]
}
```

`http://localhost:8099/winner`

```json
{
    "count": 14,
    "vote": "Hillary Clinton"
}
```

## Build Artifact

This project is continuously built and tested on every code check-in to GitHub. If all tests pass, the resulting Spring Boot JAR is stored in the [voter-service-artifacts](https://github.com/garystafford/voter-service-artifacts) GitHub repository.

## README

- [Accessing MongoDB Data with REST](https://spring.io/guides/gs/accessing-mongodb-data-rest/)
- [Spring Boot Testing](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing)
