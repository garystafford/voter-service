#!/bin/sh

# Deploy to Docker for AWS Swarm (swarm mode)

set -e

docker stack deploy --compose-file=docker-compose-aws.yml monitoring_stack

echo "Letting services start-up..."
sleep 5

docker stack ls
docker stack ps monitoring_stack --no-trunc
docker service ls

echo "Script completed..."
