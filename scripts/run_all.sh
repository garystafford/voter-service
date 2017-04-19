#!/bin/sh

# Runs all project scripts...

set -e

echo "Deploying Stack: Widget, MongoDB..."
sh ./stack_deploy.sh

echo "Waiting for Stack to be ready..."
sh ./stack_validate.sh

eval $(docker-machine env manager1)
