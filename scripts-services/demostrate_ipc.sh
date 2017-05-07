#!/bin/sh

# Demostrate API calls for REST HTTP IPC and RPC IPC via API Gateway
# Requires HTTPie
# Requires all services are running

set -e

API_GATEWAY="http://localhost:8080"
ELECTION="2016%20Presidential%20Election"

echo ""
echo "Simulating candidates..."
http ${API_GATEWAY}/candidate/simulation && sleep 2
http ${API_GATEWAY}/candidate/candidates/summary/election/${ELECTION} && sleep 2

echo ""
echo "Simulating voting using REST HTTP IPC..."
http ${API_GATEWAY}/voter/simulation/election/${ELECTION} && sleep 2
http ${API_GATEWAY}/voter/results && sleep 4
http ${API_GATEWAY}/voter/winners && sleep 2

echo ""
echo "Simulating voting using message-based RPC IPC..."
http ${API_GATEWAY}/voter/simulation/rpc/election/${ELECTION} && sleep 2
http ${API_GATEWAY}/voter/results && sleep 2
http ${API_GATEWAY}/voter/winners && sleep 2

echo ""
echo "Script completed..."
