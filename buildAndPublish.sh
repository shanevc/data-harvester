#!/usr/bin/env bash
mvn clean install &&
docker image build -t svancoller/data-harvester . &&
docker push svancoller/data-harvester
