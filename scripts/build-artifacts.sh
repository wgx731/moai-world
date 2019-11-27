#!/bin/bash

echo "[MOAI:BUILD] source config ..."
source ${PWD}/scripts/config.env || exit 1

echo "[MOAI:BUILD] building artifacts ..."
$PWD/gradlew clean build -x test -x spotbugsMain -x spotbugsTest -q || exit 1
