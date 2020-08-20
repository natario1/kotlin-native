#!/bin/sh

REVISION='adeef192947fbc0f68fa14a6c494c8df32177508'
REPO='https://github.com/google/googletest.git'
PREFIX='third_party/googletest/googletest'

cd `git rev-parse --show-toplevel`
echo "Updating googletest to ${REVISION}"
git subtree pull --prefix="$PREFIX" "$REPO" "$REVISION" --squash