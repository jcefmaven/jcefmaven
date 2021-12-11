#!/bin/bash

if [ ! $# -eq 3 ]
  then
    echo "Usage: ./upload_artifacts.sh <build_meta_url> <repo_url> <repo_id>"
    echo ""
    echo "build_meta_url: The url to download build_meta.json from"
    echo "repo_url: The repository to push to"
    echo "repo_id: A short identifier for the repo to push to"
    exit 1
fi

#CD to dir of this script
cd "$( dirname "$0" )"

echo "########################################################################"
echo "# Upload errors can be ignored when the artifact is already published! #"
echo "########################################################################"

#Print build meta location
echo "Initializing for build from $1..."

#Download build_meta.json and import to local environment
export $(curl -s -L $1 | jq -r "to_entries|map(\"\(.key)=\(.value|tostring)\")|.[]")

#Move artifacts to a non-protected folder
rm -rf upload
mkdir upload
cp out/* upload/
cd upload

#Set JOGL information (also set in scripts/generate_maven_builds.sh!)
export jogl_build=v2.4.0-rc-20210111

echo "Uploading maven artifacts for $release_tag..."

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jogl-all-$jogl_build.pom -Dfile=jogl-all-$jogl_build.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=gluegen-rt-$jogl_build.pom -Dfile=gluegen-rt-$jogl_build.jar

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-api-$release_tag.pom -Dfile=jcef-api-$release_tag.jar -Djavadoc=jcef-api-$release_tag-javadoc.jar -Dsources=jcef-api-$release_tag-sources.jar

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcefmaven-$release_tag.pom -Dfile=jcefmaven-$release_tag.jar -Djavadoc=jcefmaven-$release_tag-javadoc.jar -Dsources=jcefmaven-$release_tag-sources.jar

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-linux-amd64-$release_tag.pom -Dfile=jcef-natives-linux-amd64-$release_tag.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-linux-arm64-$release_tag.pom -Dfile=jcef-natives-linux-arm64-$release_tag.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-linux-i386-$release_tag.pom -Dfile=jcef-natives-linux-i386-$release_tag.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-linux-arm-$release_tag.pom -Dfile=jcef-natives-linux-arm-$release_tag.jar

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-macosx-amd64-$release_tag.pom -Dfile=jcef-natives-macosx-amd64-$release_tag.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-macosx-arm64-$release_tag.pom -Dfile=jcef-natives-macosx-arm64-$release_tag.jar

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-windows-amd64-$release_tag.pom -Dfile=jcef-natives-windows-amd64-$release_tag.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-windows-arm64-$release_tag.pom -Dfile=jcef-natives-windows-arm64-$release_tag.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-windows-i386-$release_tag.pom -Dfile=jcef-natives-windows-i386-$release_tag.jar

echo "Done uploading maven artifacts!"
