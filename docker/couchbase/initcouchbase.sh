#!/usr/bin/env bash

/entrypoint.sh couchbase-server &

until curl -I -s http://localhost:8091/ui/index.html
do
    echo 'Waiting for Couchbase to start (retrying in 3 seconds)...'
    sleep 3
done

couchbase-cli cluster-init -c localhost:8091 \
    --cluster-username=Administrator --cluster-password=password \
    -u Administrator -p password

tail -f /dev/null