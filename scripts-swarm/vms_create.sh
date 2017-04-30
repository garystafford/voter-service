#!/bin/sh

# Creates (6) VirtualBox VMs

set -e

vms=( "manager1" "manager2" "manager3"
      "worker1" "worker2" "worker3" )

# minimally sized vms for managers
for vm in ${vms[@]:0:3}
do
  docker-machine create \
    --driver virtualbox \
    --virtualbox-memory "512" \
    --virtualbox-cpu-count "1" \
    --virtualbox-disk-size "5000" \
    --engine-label purpose=consul \
    ${vm}
done

# medium sized vms for apps
for vm in ${vms[@]:3:2}
do
  docker-machine create \
    --driver virtualbox \
    --virtualbox-memory "1024" \
    --virtualbox-cpu-count "1" \
    --virtualbox-disk-size "20000" \
    --engine-label purpose=applications \
    ${vm}
done

# larger vm for ELK Stack
for vm in ${vms[@]:5:1}
do
  docker-machine create \
    --driver virtualbox \
    --virtualbox-memory "3072" \
    --virtualbox-cpu-count "2" \
    --virtualbox-disk-size "20000" \
    --engine-label purpose=logging \
    ${vm}
done

# fix vm.max when ELK Stack is installed on worker3
docker-machine ssh worker3 sudo sysctl -w vm.max_map_count=262144
docker-machine ssh worker3 sudo sysctl -n vm.max_map_count

docker-machine ls

echo "Script completed..."
