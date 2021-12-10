#!/bin/bash

if [ ! $# -eq 2 ]
  then
    echo "Usage: ./upload_artifacts.sh <build_meta_url>"
    echo ""
    echo "build_meta_url: The url to download build_meta.json from"
    exit 1
fi

#CD to dir of this script
cd "$( dirname "$0" )"

#Print build meta location
echo "Initializing for build from $1..."

#Download build_meta.json and import to local environment
export $(curl -s -L $1 | jq -r "to_entries|map(\"\(.key)=\(.value|tostring)\")|.[]")

echo "Uploading maven artifacts for $release_tag..."

mvn gpg:sign-and-deploy-file -DpomFile=jcef-api-$release_tag.pom -Dfile=jcef-api-$release_tag.jar
mvn gpg:sign-and-deploy-file -DpomFile=jcef-api-$release_tag.pom -Dfile=jcef-api-$release_tag-sources.jar -Dclassifier=sources
mvn gpg:sign-and-deploy-file -DpomFile=jcef-api-$release_tag.pom -Dfile=jcef-api-$release_tag-javadoc.jar -Dclassifier=javadoc

echo "Done uploading maven artifacts!"
