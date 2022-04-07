#!/bin/bash

set -e

export JSR308=$(cd $(dirname "$0")/../../ && pwd)
export CHECKERFRAMEWORK=${JSR308}/checker-framework

if [[ -d ${CHECKERFRAMEWORK} ]] ; then
    (cd ${CHECKERFRAMEWORK} && git pull)
else
    BRANCH=PropertyFileHandler
    ORGANIZATION=opprop
    echo "------ Downloading checker-framework from ORGANIZATION: $ORGANIZATION, BRANCH: $BRANCH ------"
    (cd ${JSR308} && git clone -b ${BRANCH} https://github.com/"$ORGANIZATION"/checker-framework.git)
fi

(cd ${CHECKERFRAMEWORK} && source checker/bin-devel/build.sh)
