
#!/bin/sh

# Demostrate API calls for REST HTTP IPC and RPC IPC via API Gateway
# Requires HTTPie
# Requires all services are running

set -e

HOST=$1 || localhost
TEST_CYCLES=$2 || 25
API_GATEWAY="http://${HOST}:8080"
ELECTION="2016%20Presidential%20Election"

http ${API_GATEWAY}/candidate/simulation
http ${API_GATEWAY}/candidate/candidates/summary/election/${ELECTION}

echo "Using REST HTTP IPC..."
TIME1=$(date +%s)
for i in $(seq ${TEST_CYCLES})
do
  http ${API_GATEWAY}/voter/candidates/election/${ELECTION}
done
TIME2=$(date +%s)

TIME3=`expr ${TIME2} - ${TIME1}`

echo "Using Message-based RPC IPC..."
TIME4=$(date +%s)
for i in $(seq ${TEST_CYCLES})
do
  http ${API_GATEWAY}/voter/candidates/rpc/election/${ELECTION}
done
TIME5=$(date +%s)

TIME6=`expr ${TIME5} - ${TIME4}`

echo ""
echo "REST HTTP: ${TIME3} seconds for ${TEST_CYCLES} test cycles"
echo "  RPC IPC: ${TIME6} seconds for ${TEST_CYCLES} test cycles"

echo ""
echo "Script completed..."
