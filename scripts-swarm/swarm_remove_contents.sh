#!/bin/sh

# Cleans up swarm cluster by deleting all the containers, networks, and volumes
# Leaves swarm cluster intact
# --all removes all unused images, not just dangling ones!

# set -e

# remove all stacks and services
eval $(docker-machine env manager1)

for stack in $(docker stack ls | awk '{print $1}'); \
  do docker stack rm ${stack} || echo "Stacks already removed"; done

for service in $(docker service ls | awk '{print $1}'); \
  do docker service rm ${service} || echo "Services already removed"; done

# remove all containers, networks, and volumes
vms=( "manager1" "manager2" "manager3"
      "worker1" "worker2" "worker3" )

for vm in ${vms[@]}
do
  docker-machine env ${vm}
  eval $(docker-machine env ${vm})

  docker system prune -f #--all
  docker network prune -f
  docker volume prune -f

  docker stop $(docker ps -a -q) || echo "Containers already stopped"
  docker rm -f $(docker ps -a -q) || echo "Containers already removed"
done
