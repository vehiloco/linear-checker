#!/usr/bin/env bash

set -e

# Environment
# export JSR308=$(cd $(dirname "$0")/../../ && pwd)
export CF=${CHECKERFRAMEWORK}
export JAVAC=${CHECKERFRAMEWORK}/checker/bin/javac
export LINEARCHECKER=$(cd $(dirname "$0")/../ && pwd)

# Dependencies
export CLASSPATH=${LINEARCHECKER}/build/classes/java/main:${LINEARCHECKER}/build/resources/main:\
${LINEARCHECKER}/build/libs/linear-checker.jar

# hard code, kind of ugly, TODO: fix it.
DLJC=/Users/alexliu/projects/opprop/do-like-javac
CHECKER="org.checkerframework.checker.linear.LinearChecker"
STUBFILE="IvParameterSpec.astub"

# Parsing build command of the target program
build_cmd="$1"
shift
while [[ "$#" -gt 0 ]]
do
    build_cmd="$build_cmd $1"
    shift
done

WORKING_DIR=$(pwd)
cd "$WORKING_DIR"

typecheck_cmd="python3 $DLJC/dljc -t checker --checker $CHECKER --stub $STUBFILE -- $build_cmd"

echo "============ Important variables ============="
# echo "JSR308: $JSR308"
echo "CLASSPATH: $CLASSPATH"
echo "build cmd: $build_cmd"
echo "running cmd: $typecheck_cmd"
echo "=============================================="

eval "${typecheck_cmd}"

echo "---- Reminder: do not forget to clean up the project! ----"
