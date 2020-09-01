#!/usr/bin/env bash

echo "triggering wait-for-it.sh"
./wait-for-it.sh db:8091/pools/default --timeout=60 --strict -- echo "couchbase check 1"
./wait-for-it.sh db:11210 --timeout=60 --strict -- echo "couchbase check 2"
ls -la || echo
java -jar ./urlshortener-0.0.1-SNAPSHOT.jar