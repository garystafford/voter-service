#!/bin/sh

# Deploys the voter_stack locally

set -e

export ACTIVE_PROFILE=docker-local

docker-compose -f docker-compose-local.yml -p voter_stack up -d

echo "Letting services start-up..."
sleep 5

docker ps

echo "Script completed..."
