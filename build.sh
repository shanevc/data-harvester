#!/usr/bin/env bash
mvn clean install &&
docker image build -t svancoller/images:data-harvester .
