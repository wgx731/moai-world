#!/bin/bash

echo "[MOAI] source config ..."
source ${PWD}/scripts/config.env || exit 1

echo "[MOAI] building artifacts ..."
$PWD/gradlew clean build -x test -x spotbugsMain -x spotbugsTest -q || exit 1
