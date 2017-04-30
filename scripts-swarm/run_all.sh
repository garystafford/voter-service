#!/bin/sh

# Runs all project scripts...

set -e

echo "Creating VMs..."
sh ./vms_create.sh

echo "Creating Swarm..."
sh ./swarm_create.sh

echo "Creating Network and Volumes..."
sh ./ntwk_vols_create.sh

echo "Deploying Stack: Fluentd, ELK, Visualizer..."
sh ./stack_deploy.sh

echo "Waiting for Stack to be ready..."
sh ./stack_validate.sh

echo "Evaluating manager1..."
eval $(docker-machine env manager1)
