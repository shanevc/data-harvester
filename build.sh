#!/usr/bin/env bash
mvn clean install &&
docker image build -t data-harvester-0.1 .
