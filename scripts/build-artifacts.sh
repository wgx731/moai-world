#!/bin/bash

echo "[MOAI] building artifacts ..."
$PWD/gradlew clean build -x test -x spotbugsMain -x spotbugsTest asciidoctor javadoc || exit 1