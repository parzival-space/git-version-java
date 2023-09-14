#!/bin/bash
set -eo pipefail

echo "Downloading Git Version Tool..."
download_url=$(curl -Ls https://api.github.com/repos/parzival-space/git-version-java/releases/latest | jq -r .assets[0].browser_download_url)
curl -Ls "$download_url" > /git-version.jar
chmod +x /git-version.jar

echo "Switching into Workspace..."
if [ "$GITHUB_WORKSPACE" == "" ]; then
  echo "This is only for testing. If you read this, something has gone wrong!"
  GITHUB_WORKSPACE="/github/workspace"
  mkdir -p $GITHUB_WORKSPACE
  git config --global init.defaultBranch "development"
  git init $GITHUB_WORKSPACE
fi

# parse arguments
MAJOR_IDENTIFIER=${1:-'breaking:.*'}
MINOR_IDENTIFIER=${2:-'feature:.*'}
NO_HASH=${3:-''} # is --no-hash if enabled
RELEASE_BRANCH=${4:-'main'}
SNAPSHOT_BRANCH=${5:-'development'}
SUFFIX=${6:-'SNAPSHOT'}

# get previous version
PREVIOUS_VERSION=$(java -jar /git-version.jar \
                     $NO_HASH \
                     --previous-version \
                     --major-identifier "$MAJOR_IDENTIFIER" \
                     --minor-identifier "$MINOR_IDENTIFIER" \
                     --release-branch "$RELEASE_BRANCH" \
                     --snapshot-branch "$SNAPSHOT_BRANCH" \
                     --suffix "$SUFFIX" \
                     --target "$GITHUB_WORKSPACE")
echo "Previous Version: $PREVIOUS_VERSION"

# get current version
CURRENT_VERSION=$(java -jar /git-version.jar \
                    $NO_HASH \
                    --major-identifier "$MAJOR_IDENTIFIER" \
                    --minor-identifier "$MINOR_IDENTIFIER" \
                    --release-branch "$RELEASE_BRANCH" \
                    --snapshot-branch "$SNAPSHOT_BRANCH" \
                    --suffix "$SUFFIX" \
                    --target "$GITHUB_WORKSPACE")
echo "Current Version: $CURRENT_VERSION"

# return values
echo "previous-version=$PREVIOUS_VERSION" >> $GITHUB_OUTPUT
echo "version=$CURRENT_VERSION" >> $GITHUB_OUTPUT