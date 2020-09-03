#!/usr/bin/env bash

echo "triggering wait-for-it.sh"
./wait-for-it.sh db:8091/pools/default --timeout=60 --strict -- echo "couchbase check 1"
./wait-for-it.sh db:11210 --timeout=60 --strict -- echo "couchbase check 2"
ls -la || echo
java -javaagent:./apm-agent.jar \
     -Delastic.apm.service_name=my-application \
     -Delastic.apm.server_urls=https://58bf041db1fe4a27a906f18c21d533cf.apm.us-central1.gcp.cloud.es.io:443 \
     -Delastic.apm.secret_token=PN1a9ofGu5SzMzlNZi \
     -Delastic.apm.application_packages=org.example \
     -jar ./urlshortener-0.0.1-SNAPSHOT.jar