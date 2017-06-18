#!/bin/sh

# Deploy Voter Stack to Docker for AWS Swarm (swarm mode)

set -e

export ACTIVE_PROFILE=docker-local
export ENVIRONMENT=development

docker stack deploy --compose-file=docker-compose-aws.yml voter_stack

echo "Letting services start-up..."
sleep 5

docker stack ls
docker stack ps voter_stack --no-trunc
docker service ls

echo "Script completed..."
