#!/bin/bash

echo "[MOAI] building artifacts ..."
$PWD/gradlew clean build -x test -x spotbugsMain -x spotbugsTest || exit 1
