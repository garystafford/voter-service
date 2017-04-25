#!/bin/sh

# Deploys the voter_stack locally

set -e

export ACTIVE_PROFILE=docker-local

docker-compose --file docker-compose-local.yml up -d

echo "Letting services start-up..."
sleep 5

docker ps

echo "Script completed..."
