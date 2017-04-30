#!/bin/sh

# Deploys the voter_stack to the Docker swarm cluster

set -e

eval $(docker-machine env manager1)

export ACTIVE_PROFILE=docker-local
export ENVIRONMENT=development

docker stack deploy --compose-file=docker-compose.yml voter_stack

echo "Letting services start-up..."
sleep 5

docker stack ls
docker stack ps widget_stack --no-trunc
docker service ls

echo "Script completed..."
