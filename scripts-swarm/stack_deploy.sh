#!/bin/sh

# Deploys the monitoring_stack to the Docker swarm cluster

set -e

eval $(docker-machine env manager1)

export CONSUL_SERVER=$(docker-machine ip $(docker node ls | grep Leader | awk '{print $3}'))

docker stack deploy --compose-file=docker-compose.yml monitoring_stack

echo "Letting services start-up..."
sleep 5

docker stack ls
docker stack ps monitoring_stack --no-trunc
docker service ls

echo "Script completed..."
