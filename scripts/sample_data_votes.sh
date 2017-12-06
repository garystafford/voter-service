#!/bin/bash

# Drop all votes and POST new votes to CosmosDB

url="https://api.voter-demo.com"

echo "Dropping all existing votes documents from votes DB..."
curl --request POST --url ${url}/voter/drop/votes

echo "Creating sample voting data..."
curl --request GET --url ${url}/voter/simulation/2016%20Presidential%20Election
