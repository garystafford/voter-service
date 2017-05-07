#!/bin/sh

# Demostrate API calls for REST HTTP IPC and RPC IPC via API Gateway
# Requires HTTPie
# Requires all services are running

set -e

echo "\nSimulating candidates...\n"
http http://localhost:8080/candidate/simulation && sleep 2
http http://localhost:8080/candidate/candidates/summary/election/2016%20Presidential%20Election && sleep 2

echo "\nSimulating voting using REST HTTP IPC...\n"
http http://localhost:8080/voter/simulation/election/2016%20Presidential%20Election && sleep 2
http http://localhost:8080/voter/results && sleep 4
http http://localhost:8080/voter/winners && sleep 2

echo "\nSimulating voting using message-based RPC IPC...\n"
http http://localhost:8080/voter/simulation/rpc/election/2016%20Presidential%20Election && sleep 2
http http://localhost:8080/voter/results && sleep 2
http http://localhost:8080/voter/winners && sleep 2

echo "\nScript completed...\n"
