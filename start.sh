#!/usr/bin/env bash
docker container run --name data-harvester --rm -d -p 8083:8080 svancoller/images:data-harvester