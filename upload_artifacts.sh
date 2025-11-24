#!/bin/bash
set -e

if [ ! $# -eq 2 ]
  then
    echo "Usage: ./upload_artifacts.sh <build_meta_url> <mvn_version>"
    echo "Release repo url should NOT end in /!"
    exit 1
fi

#CD to dir of this script
cd "$( dirname "$0" )"

#Set build info
. scripts/set_build_info.sh $1 $2

#Move artifacts to a non-protected folder
rm -rf upload
mkdir upload
cp out/* upload/

echo "Uploading maven artifacts for $mvn_version..."

#Sign Jogamp libraries
./sign_artifact.sh me.friwi jogl-all $jogl_build
./sign_artifact.sh me.friwi gluegen-rt $jogl_build

#Check for duplicates among jogamp libraries
metadata=$(curl -s "https://repo1.maven.org/maven2/me/friwi/jogl-all/maven-metadata.xml")

if echo "$metadata" | grep -q "<version>$jogl_build</version>"; then
  # Skip jogamp uploading, version already exists
  echo "Jogamp version already exists on central - skipping!"
  rm jogl-all*
  rm gluegen-rt*
fi

#Sign API
./sign_artifact.sh me.friwi jcef-api $release_tag

#Sign jcefmaven
./sign_artifact.sh me.friwi jcefmaven $mvn_version

#Sign linux natives
./sign_artifact.sh me.friwi jcef-natives-linux-amd64 $release_tag
./sign_artifact.sh me.friwi jcef-natives-linux-arm64 $release_tag
./sign_artifact.sh me.friwi jcef-natives-linux-arm $release_tag

#Sign windows natives
./sign_artifact.sh me.friwi jcef-natives-windows-amd64 $release_tag
./sign_artifact.sh me.friwi jcef-natives-windows-arm64 $release_tag
./sign_artifact.sh me.friwi jcef-natives-windows-i386 $release_tag

#Sign macosx natives
./sign_artifact.sh me.friwi jcef-natives-macosx-amd64 $release_tag
./sign_artifact.sh me.friwi jcef-natives-macosx-arm64 $release_tag

#Upload all
mvn central-publishing-maven-plugin:deploy -DstagingDirectory=./upload

echo "Done uploading maven artifacts!"
