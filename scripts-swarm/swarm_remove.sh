#!/bin/sh

# Reset Docker swarm cluster
# Remove all (6) VirtualBox VMs from swarm cluster

# set -e

vms=( "manager1" "manager2" "manager3"
      "worker1" "worker2" "worker3" )

for vm in ${vms[@]}
do
  eval $(docker-machine env ${vm})
  docker swarm leave -f
  echo "Node ${vm} has left the swarm cluster..."
done

eval $(docker-machine env manager1)

for vm in ${vms[@]:1}
do
  docker node rm ${vm}
  echo "Node ${vm} was removed from the swarm cluster..."
done

docker node rm manager1
docker node ls

echo "Script completed..."
