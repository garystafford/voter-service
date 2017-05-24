#!/bin/sh

# Checks if Voter Service is up

HOST_IP=${1:-$(docker-machine ip worker2)}
ATTEMPTS=10
SLEEPTIME=15

until curl -s --head "${HOST_IP}:8080/voter/info";
do
  echo "Attempt ${ATTEMPTS}..."

  if [ $ATTEMPTS -eq 0 ]
  then
    break
  fi

  echo "Waiting ${SLEEPTIME} more seconds to see if things are working..."

  sleep $SLEEPTIME
  let ATTEMPTS-=1
done

curl "${HOST_IP}:8099/voter/health"
echo ""
curl "${HOST_IP}:8099/voter/info"

echo ""
echo "Script completed..."
