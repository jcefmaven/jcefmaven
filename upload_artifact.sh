#!/bin/bash

if [ ! $# -eq 3 ]
  then
    echo "Usage: ./upload_artifact.sh <groupId> <artifactId> <version>"
    echo "Release repo url should NOT end in /!"
    exit 1
fi

groupId=$1
artifactId=$2
version=$3

#CD to the upload dir
cd "$( dirname "$0" )" && cd upload

echo "Signing $artifactId-$version..."
mvn gpg:sign -DpomFile=$artifactId-$version.pom -Dfile=$artifactId-$version.jar -Djavadoc=$artifactId-$version-javadoc.jar -Dsources=$artifactId-$version-sources.jar

echo "Pushing $artifactId-$version..."
mvn deploy -DdeploymentName=JCefMaven -DignorePublishedComponents=true -DpomFile=$artifactId-$version.pom -Dfile=$artifactId-$version.jar -Djavadoc=$artifactId-$version-javadoc.jar -Dsources=$artifactId-$version-sources.jar

