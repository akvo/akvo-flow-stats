#!/usr/bin/env bash

set -eu

arg="${1:--M:nREPL}"
if [[ "$arg" == "cider" ]]; then
    arg="-M:cider"
fi

docker run \
       --rm \
       -p 36333:36333 \
       -v "$(pwd):/app" \
       -v "$(pwd)/../akvo-flow-server-config/:/akvo-flow-server-config" \
       akvo-flow-stats "$arg"
