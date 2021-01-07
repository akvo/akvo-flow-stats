#!/usr/bin/env bash

set -eu

docker build \
       -t akvo-flow-stats \
       .
