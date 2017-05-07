
#!/bin/sh

# Demostrate API calls for REST HTTP IPC and RPC IPC via API Gateway
# Requires HTTPie
# Requires all services are running

set -e

API_GATEWAY="http://localhost:8080"
ELECTION="2016%20Presidential%20Election"
TEST_CYCLES=100

http ${API_GATEWAY}/candidate/simulation
http ${API_GATEWAY}/candidate/candidates/summary/election/${ELECTION}

echo "\nUsing REST HTTP IPC...\n"
TIME1=$(date +%s)
for i in $(seq ${TEST_CYCLES})
do
  http ${API_GATEWAY}/voter/candidates/election/${ELECTION}
done
TIME2=$(date +%s)

TIME3=`expr ${TIME2} - ${TIME1}`

echo "\nUsing Message-based RPC IPC...\n"
TIME4=$(date +%s)
for i in $(seq ${TEST_CYCLES})
do
  http ${API_GATEWAY}/voter/candidates/rpc/election/${ELECTION}
done
TIME5=$(date +%s)

TIME6=`expr ${TIME4} - ${TIME5}`

echo "\nREST HTTP: ${TIME3} seconds for ${TEST_CYCLES} test cycles"
echo "  RPC IPC: ${TIME3} seconds for ${TEST_CYCLES} test cycles\n"

echo "\nScript completed...\n"
