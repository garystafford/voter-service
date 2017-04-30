#!/bin/sh

# Runs all project scripts...

set -e

echo "Deploying Voter Stack..."
sh ./stack_deploy.sh

echo "Waiting for Voter Stack to be ready..."
sh ./stack_validate.sh

echo "Setting Docker to execute commands against manager1..."
eval $(docker-machine env manager1)
