#!/bin/bash

echo "[MOAI] docker clean up ..."

echo "[MOAI] docker clean up - image ..."
docker rmi $(docker images --filter "dangling=true" -q --no-trunc)

echo "[MOAI] docker clean up - container ..."
docker rm $(docker ps -qa --no-trunc --filter "status=exited")
docker ps -f status=exited  | grep "\-cache-" | awk '{print $1}' | xargs docker rm

echo "[MOAI] docker clean up - volume ..."
docker volume rm $(docker volume ls -qf dangling=true)
docker volume prune -f

echo "[MOAI] docker clean up - network ..."
docker network rm $(docker network ls | grep "bridge" | awk '/ / { print $1 }')
docker network prune -f

echo "[MOAI] gradle clean up ..."
rm -rf $PWD/build
rm -rf $PWD/.gradle