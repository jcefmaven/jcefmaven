#!/bin/bash

if [ ! $# -eq 6 ]
  then
    echo "Usage: ./upload_artifact.sh <stageRepoUrl> <releaseRepoUrl> <repoId> <groupId> <artifactId> <version>"
    echo "Release repo url should NOT end in /!"
    exit 1
fi

stageRepoUrl=$1
releaseRepoUrl=$2
repoId=$3
groupId=$4
artifactId=$5
version=$6

pathGroupId=$(sed 's|\.|\/|g' <<< $groupId)
targetUrl=$releaseRepoUrl/$pathGroupId/$artifactId/$version/$artifactId-$version.jar

#On GitHub this check will always fail. Single pushes may return an error,
#but that is not really an error worth solving. On OSSRH, to prevent conflicts,
#no builds will be staged that are already published.
if curl --output /dev/null --silent --fail -r 0-0 "$targetUrl"; then
    echo "Artifact $artifactId-$version aready pushed - skipping!"
    exit 0
fi

echo "Pushing $artifactId-$version..."
mvn gpg:sign-and-deploy-file -Durl=$stageRepoUrl -DrepositoryId=$repoId -DpomFile=$artifactId-$version.pom -Dfile=$artifactId-$version.jar -Djavadoc=$artifactId-$version-javadoc.jar -Dsources=$artifactId-$version-sources.jar

