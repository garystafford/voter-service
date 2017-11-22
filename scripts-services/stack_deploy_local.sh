#!/bin/sh

# Deploys the voter services locally
# Not in swarm mode

set -e

export ACTIVE_PROFILE=docker-local
export ENVIRONMENT=development

docker-compose pull # ensure latest version are pulled...

docker-compose \
  -f docker-compose-local.yml \
  -p demostack up \
  --force-recreate -d

docker image prune -f # clean up danglers...

echo "Letting services start-up..."
sleep 5

docker ps

sh ./stack_validate.sh localhost

echo "Script completed..."
