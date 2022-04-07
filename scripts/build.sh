#!/bin/bash

set -e

CRYPTO_CHECKER_PATH="/crypto-checker"

CORPUS_FILE_NAME=$1
BENCHMARK_PATH="$CRYPTO_CHECKER_PATH/benchmarks"
CORPUS_FILE_PATH="$BENCHMARK_PATH/$CORPUS_FILE_NAME"

RUN_CORPUS_SCRIPT_PATH="$CRYPTO_CHECKER_PATH/scripts/run-corpus.py"
EXECUTABLE_PATH="$CRYPTO_CHECKER_PATH/scripts/run-dljc.sh"

# Build the crypto-checker
cd ${CRYPTO_CHECKER_PATH} && ./gradlew assemble && ./gradlew copyDependencies &&./gradlew publishToMavenLocal

python2 ${RUN_CORPUS_SCRIPT_PATH} --corpus-file ${CORPUS_FILE_PATH}  --executable ${EXECUTABLE_PATH}
