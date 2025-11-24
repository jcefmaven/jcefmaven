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

#Check for duplicates among jogamp libraries
metadata=$(curl -s "https://repo1.maven.org/maven2/me/friwi/jogl-all/maven-metadata.xml")

if echo "$metadata" | grep -q "<version>$jogl_build</version>"; then
  # Skip jogamp uploading, version already exists
  echo "Jogamp version already exists on central - skipping!"
  rm jogl-all*
  rm gluegen-rt*
fi

# Sign all artifacts
echo "Signing artifacts..."
for file in ./upload/*
do
  echo "Signing $file..."
  gpg --detach-sign --armor --no-tty "$file"
done


#Upload all
echo "Uploading artifacts..."
mvn central-publishing-maven-plugin:deploy -DstagingDirectory=./upload

echo "Done uploading maven artifacts!"
