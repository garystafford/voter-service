[![Build Status](https://travis-ci.org/garystafford/fav-color-service.svg?branch=master)](https://travis-ci.org/garystafford/fav-color-service)

# Favorite Color Service

_Work in Progress_

## Introduction

A simple Spring Boot RESTful microservice, backed by MongoDB. Part of an upcoming article on CI/CD with Spring Boot, HashiCorp product-line, and AWS.

## Quick Start

To clone, build, test, and run the Favorite Color Spring Boot service locally, requires MongoDB to be pre-installed and running on port `27017`.

```bash
git clone https://github.com/garystafford/fav-color-service.git
cd fav-color-service
./gradlew clean cleanTest build && \
    java -jar build/libs/fav-color-0.1.0.jar
```

## Primary Service Endpoints
Out of the box, the service runs on `localhost`, port `8091`. By default, the service looks for MongoDB on `localhost`, port `27017`.

- Purge and Add New Sample Data (GET): <http://localhost:8091/seeder>
- List Color Choices (GET): <http://localhost:8091/colors>
- Submit Favorite Color (POST): <http://localhost:8091/colors>
- View Results Summary (GET): <http://localhost:8091/results>
- View Favorite Color (GET): <http://localhost:8091/favorite>
- Service Health (GET): <http://localhost:8091/health>
- Service Metrics (GET): <http://localhost:8091/metrics>
- Other [Spring Actuator](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) endpoints include: `/mappings`, `/env`, `/configprops`, etc.
- Other [HATEOAS](https://spring.io/guides/gs/rest-hateoas) endpoints for `/colors` include: DELETE, PATCH, PUT, page sort, size, etc.

## POST Color Choice:
- HTTPie: `http POST localhost:8091/colors color=blue`
- cURL: `curl -X POST -H "Content-Type: application/json" -d '{ "color": "blue" }' "http://localhost:8091/colors"`
- wget: `wget --method POST --header 'content-type: application/json' --body-data '{ "color": "blue" }' --output-document - http://localhost:8091/colors`

## README
- [Accessing MongoDB Data with REST](https://spring.io/guides/gs/accessing-mongodb-data-rest/)
- [Spring Boot Testing](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing)
