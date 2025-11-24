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

echo "Deploying maven artifacts for $mvn_version..."

# Sign all artifacts
echo "Signing artifacts..."
for file in ./upload/*
do
  echo "Signing $file..."
  gpg --detach-sign --armor --no-tty "$file"
  md5sum "$file" | awk '{ print $1 }' > "$file".md5
  sha1sum "$file" | awk '{ print $1 }' > "$file".sha1
  md5sum "$file".asc | awk '{ print $1 }' > "$file".asc.md5
  sha1sum "$file".asc | awk '{ print $1 }' > "$file".asc.sha1
done

chmod +x upload_artifact.sh

#Check for duplicates among jogamp libraries
metadata=$(curl -s "https://repo1.maven.org/maven2/me/friwi/jogl-all/maven-metadata.xml")

if echo "$metadata" | grep -q "<version>$jogl_build</version>"; then
  # Skip jogamp uploading, version already exists
  echo "Jogamp version already exists on central - skipping!"
  rm jogl-all*
  rm gluegen-rt*
else
  #Upload Jogamp libraries
  ./upload_artifact.sh me.friwi jogl-all $jogl_build
  ./upload_artifact.sh me.friwi gluegen-rt $jogl_build
fi

#Upload API
./upload_artifact.sh me.friwi jcef-api $release_tag

#Upload jcefmaven
./upload_artifact.sh me.friwi jcefmaven $mvn_version

#Upload linux natives
./upload_artifact.sh me.friwi jcef-natives-linux-amd64 $release_tag
./upload_artifact.sh me.friwi jcef-natives-linux-arm64 $release_tag
./upload_artifact.sh me.friwi jcef-natives-linux-arm $release_tag

#Upload windows natives
./upload_artifact.sh me.friwi jcef-natives-windows-amd64 $release_tag
./upload_artifact.sh me.friwi jcef-natives-windows-arm64 $release_tag
./upload_artifact.sh me.friwi jcef-natives-windows-i386 $release_tag

#Upload macosx natives
./upload_artifact.sh me.friwi jcef-natives-macosx-amd64 $release_tag
./upload_artifact.sh me.friwi jcef-natives-macosx-arm64 $release_tag

echo "Done uploading maven artifacts!"
