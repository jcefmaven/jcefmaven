#!/bin/bash

if [ ! $# -eq 4 ]
  then
    echo "Usage: ./upload_artifacts.sh <build_meta_url> <stageRepoUrl> <releaseRepoUrl> <repo_id>"
    echo "Release repo url should NOT end in /!"
    exit 1
fi

#CD to dir of this script
cd "$( dirname "$0" )"

#Set build info
. scripts/set_build_info.sh $1

#Move artifacts to a non-protected folder
rm -rf upload
mkdir upload
cp out/* upload/

echo "Uploading maven artifacts for $mvn_version+$release_tag..."

#Upload Jogamp libraries
./upload_artifact.sh $2 $3 $4 me.friwi jogl-all $jogl_build
./upload_artifact.sh $2 $3 $4 me.friwi gluegen-rt $jogl_build

#Upload API
./upload_artifact.sh $2 $3 $4 me.friwi jcef-api $release_tag

#Upload jcefmaven
./upload_artifact.sh $2 $3 $4 me.friwi jcefmaven $mvn_version+$release_tag

#Upload linux natives
./upload_artifact.sh $2 $3 $4 me.friwi jcef-natives-linux-amd64 $release_tag
./upload_artifact.sh $2 $3 $4 me.friwi jcef-natives-linux-arm64 $release_tag
./upload_artifact.sh $2 $3 $4 me.friwi jcef-natives-linux-i386 $release_tag
./upload_artifact.sh $2 $3 $4 me.friwi jcef-natives-linux-arm $release_tag

#Upload windows natives
./upload_artifact.sh $2 $3 $4 me.friwi jcef-natives-windows-amd64 $release_tag
./upload_artifact.sh $2 $3 $4 me.friwi jcef-natives-windows-arm64 $release_tag
./upload_artifact.sh $2 $3 $4 me.friwi jcef-natives-windows-i386 $release_tag

#Upload macosx natives
./upload_artifact.sh $2 $3 $4 me.friwi jcef-natives-macosx-amd64 $release_tag
./upload_artifact.sh $2 $3 $4 me.friwi jcef-natives-macosx-arm64 $release_tag

echo "Done uploading maven artifacts!"
