#!/usr/bin/env bash
mvn clean install &&
docker image build -t svancoller/data-harvester . &&
docker container run --name data-harvester --rm -d -p 8083:8080 svancoller/data-harvester
