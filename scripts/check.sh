#!/usr/bin/env bash

set -e

# Environment
export JSR308=$(cd $(dirname "$0")/../../ && pwd)
export CF=${JSR308}/checker-framework
export JAVAC=${CF}/checker/bin/javac
export CRYPTOCHECKER=$(cd $(dirname "$0")/../ && pwd)

# Dependencies
export CLASSPATH=${CRYPTOCHECKER}/build/classes/java/main:${CRYPTOCHECKER}/build/resources/main:\
${CRYPTOCHECKER}/build/libs/crypto-checker.jar

# Command
DEBUG=""
CHECKER="org.checkerframework.checker.crypto.CryptoChecker"
STUBFILE="hardwarebacked.astub"

declare -a ARGS
for i in "$@" ; do
    if [[ ${i} == "-d" ]] ; then
        echo "Typecheck using debug mode. Listening at port 5005. Waiting for connection...."
        DEBUG="-J-Xdebug -J-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
        continue
    fi
    ARGS[${#ARGS[@]}]="$i"
done

cmd=""

if [[ "$DEBUG" == "" ]]; then
    cmd="$JAVAC -cp "${CLASSPATH}" -processor "${CHECKER}"  -AnonNullStringsConcatenation "${ARGS[@]}""
else
    cmd="$JAVAC "${DEBUG}" -cp "${CLASSPATH}" -processor "${CHECKER}" -AnonNullStringsConcatenation "${ARGS[@]}""
fi

eval "${cmd}"
