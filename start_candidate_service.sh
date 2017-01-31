#!/bin/sh

# Pull and start Candidate service in the background
# Required to execute Voter unit tests

set -ex

git clone --depth 1 --branch build-artifacts \
    "https://github.com/garystafford/candidate-service.git"

cd candidate-service/
mv candidate-service-*.jar candidate-service.jar

nohup \
  java -Djava.security.egd=file:/dev/./urandom \
  -jar candidate-service.jar \
  > /dev/null 2>&1 &

sleep 10
