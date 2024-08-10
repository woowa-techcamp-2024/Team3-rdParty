#!/bin/bash
# implementation: team3-rdParty pre-push hook
# This script is based on a LGPL 3.0 licensed script.
#
# Original Script Copyright (C) 2023 Lablup Inc.
# Modifications Copyright (C) 2024 mirageoasis
#
# This script is modified under the same license, the GNU Lesser General Public License v3.0.

BASE_PATH=$(cd "$(dirname "$0")"/.. && pwd)

CURRENT_COMMIT=$(git rev-parse --short HEAD)
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [ -n "$(echo "$CURRENT_BRANCH" | sed -n '/^[[:digit:]]\{1,\}\.[[:digit:]]\{1,\}/p')" ]; then
  # if we are on the release branch, use it as the base branch.
  BASE_BRANCH="$CURRENT_BRANCH"
else
  BASE_BRANCH="main"
fi
if [ "$1" != "origin" ]; then
  # extract the owner name of the target repo
  ORIGIN="$(echo "$1" | grep -o '://[^/]\+/[^/]\+/' | grep -o '/[^/]\+/$' | tr -d '/')"
  cleanup_remote() {
    git remote remove "$ORIGIN"
  }
  trap cleanup_remote EXIT
  git remote add "$ORIGIN" "$1"
  git fetch -q --depth=1 --no-tags "$ORIGIN" "$BASE_BRANCH"
else
  ORIGIN="origin"
fi
echo "Performing lint and check on ${ORIGIN}/${BASE_BRANCH}..HEAD@${CURRENT_COMMIT} ..."

# Gradle을 사용하여 spotlessCheck 검사 수행
./gradlew spotlessCheck

# 필요한 다른 검사 수행
# ./gradlew 다른_검사
