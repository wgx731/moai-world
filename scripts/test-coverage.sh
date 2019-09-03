#!/bin/bash

echo "[MOAI] testing and generating coverage report ..."
$PWD/gradlew clean check jacocoTestReport jacocoTestCoverageVerification || exit 1
