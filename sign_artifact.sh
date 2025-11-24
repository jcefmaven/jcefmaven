#!/bin/bash
set -e

if [ ! $# -eq 3 ]
  then
    echo "Usage: ./sign_artifact.sh <groupId> <artifactId> <version>"
    echo "Release repo url should NOT end in /!"
    exit 1
fi

groupId=$1
artifactId=$2
version=$3

#CD to the upload dir
cd "$( dirname "$0" )" && cd upload

echo "Signing $artifactId-$version..."
for file in $artifactId-$version.jar $artifactId-$version-sources.jar $artifactId-$version-javadoc.jar $artifactId-$version.pom
do
  gpg --detach-sign --armor "$file"
done