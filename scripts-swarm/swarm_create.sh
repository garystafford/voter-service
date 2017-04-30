#!/bin/sh

# Creates Docker swarm using (6) VirtualBox VMs

set -e

vms=( "manager1" "manager2" "manager3"
      "worker1" "worker2" "worker3" )

SWARM_MANAGER_IP=$(docker-machine ip ${vms[@]:0:1})
echo ${SWARM_MANAGER_IP}

docker-machine ssh manager1 \
  "docker swarm init \
  --advertise-addr ${SWARM_MANAGER_IP}"

eval $(docker-machine env manager1)

MANAGER_SWARM_JOIN=$(docker-machine ssh ${vms[@]:0:1} "docker swarm join-token manager")
MANAGER_SWARM_JOIN=$(echo ${MANAGER_SWARM_JOIN} | grep -E "(docker).*(2377)" -o)
MANAGER_SWARM_JOIN=$(echo ${MANAGER_SWARM_JOIN//\\/''})
echo ${MANAGER_SWARM_JOIN}

# two other manager nodes
for vm in ${vms[@]:1:2}
do
  docker-machine ssh ${vm} ${MANAGER_SWARM_JOIN}
done

WORKER_SWARM_JOIN=$(docker-machine ssh ${vms[@]:0:1} "docker swarm join-token worker")
WORKER_SWARM_JOIN=$(echo ${WORKER_SWARM_JOIN} | grep -E "(docker).*(2377)" -o)
WORKER_SWARM_JOIN=$(echo ${WORKER_SWARM_JOIN//\\/''})
echo ${WORKER_SWARM_JOIN}

# three worker nodes
for vm in ${vms[@]:3:3}
do
  docker-machine ssh ${vm} ${WORKER_SWARM_JOIN}
done

docker node ls

echo "Script completed..."
