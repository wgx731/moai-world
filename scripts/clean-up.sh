#!/bin/bash

echo "[MOAI:CLEAN] docker clean up ..."

echo "[MOAI:CLEAN] docker clean up - image ..."
docker rmi $(docker images --filter "dangling=true" -qa --no-trunc)

echo "[MOAI:CLEAN] docker clean up - container ..."
docker rm $(docker ps -qa --no-trunc --filter "status=exited")
docker ps -f status=exited  | grep "\-cache-" | awk '{print $1}' | xargs docker rm

echo "[MOAI:CLEAN] docker clean up - volume ..."
docker volume rm $(docker volume ls -qf dangling=true)
docker volume prune -f

echo "[MOAI:CLEAN] docker clean up - network ..."
docker network rm $(docker network ls | grep "bridge" | awk '/ / { print $1 }')
docker network prune -f

echo "[MOAI:CLEAN] gradle clean up ..."
rm -rf $PWD/build
rm -rf $PWD/.gradle
$PWD/gradlew clean idea