
#!/bin/sh

# Demostrate API calls for REST HTTP IPC and RPC IPC via API Gateway
# Requires HTTPie
# Requires all services are running

set -e

http http://localhost:8080/candidate/simulation
http http://localhost:8080/candidate/candidates/summary/election/2016%20Presidential%20Election

echo "\nUsing REST HTTP IPC...\n"
echo $(date +%s)
for i in {1..50}
do
  http http://localhost:8080/voter/candidates/election/2016%20Presidential%20Election
done
echo $(date +%s)

echo "\nUsing Message-based RPC IPC...\n"
echo $(date +%s)
for i in {1..50}
do
  http http://localhost:8080/voter/candidates/rpc/election/2016%20Presidential%20Election
done
echo $(date +%s)

echo "\nScript completed...\n"
