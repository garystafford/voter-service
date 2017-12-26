#!/bin/bash

# drop all votes and post new votes to mongodb or cosmos db

#url="https://api.voter-demo.com"
url="http://localhost:8099"

echo "Dropping all existing votes documents from votes DB..."
echo "POSTing ${url}/voter/drop/votes"
curl --request POST --url ${url}/voter/drop/votes

echo "Creating sample voting data..."
echo "POSTing ${url}/voter/simulation/2016%20Presidential%20Election"
curl --request GET --url ${url}/voter/simulation/2016%20Presidential%20Election
