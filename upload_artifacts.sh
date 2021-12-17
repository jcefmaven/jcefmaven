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

#Set build info
. scripts/set_build_info.sh $1

#Move artifacts to a non-protected folder
rm -rf upload
mkdir upload
cp out/* upload/
cd upload

echo "Uploading maven artifacts for $mvn_version+$release_tag..."

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jogl-all-$jogl_build.pom -Dfile=jogl-all-$jogl_build.jar -Djavadoc=jogl-all-$jogl_build-javadoc.jar -Dsources=jogl-all-$jogl_build-sources.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=gluegen-rt-$jogl_build.pom -Dfile=gluegen-rt-$jogl_build.jar -Djavadoc=gluegen-rt-$jogl_build-javadoc.jar -Dsources=gluegen-rt-$jogl_build-sources.jar

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-api-$release_tag.pom -Dfile=jcef-api-$release_tag.jar -Djavadoc=jcef-api-$release_tag-javadoc.jar -Dsources=jcef-api-$release_tag-sources.jar

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcefmaven-$mvn_version+$release_tag.pom -Dfile=jcefmaven-$mvn_version+$release_tag.jar -Djavadoc=jcefmaven-$mvn_version+$release_tag-javadoc.jar -Dsources=jcefmaven-$mvn_version+$release_tag-sources.jar

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-linux-amd64-$release_tag.pom -Dfile=jcef-natives-linux-amd64-$release_tag.jar -Djavadoc=jcef-natives-linux-amd64-$release_tag-javadoc.jar -Dsources=jcef-natives-linux-amd64-$release_tag-sources.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-linux-arm64-$release_tag.pom -Dfile=jcef-natives-linux-arm64-$release_tag.jar -Djavadoc=jcef-natives-linux-arm64-$release_tag-javadoc.jar -Dsources=jcef-natives-linux-arm64-$release_tag-sources.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-linux-i386-$release_tag.pom -Dfile=jcef-natives-linux-i386-$release_tag.jar -Djavadoc=jcef-natives-linux-i386-$release_tag-javadoc.jar -Dsources=jcef-natives-linux-i386-$release_tag-sources.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-linux-arm-$release_tag.pom -Dfile=jcef-natives-linux-arm-$release_tag.jar -Djavadoc=jcef-natives-linux-arm-$release_tag-javadoc.jar -Dsources=jcef-natives-linux-arm-$release_tag-sources.jar

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-macosx-amd64-$release_tag.pom -Dfile=jcef-natives-macosx-amd64-$release_tag.jar -Djavadoc=jcef-natives-macosx-amd64-$release_tag-javadoc.jar -Dsources=jcef-natives-macosx-amd64-$release_tag-sources.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-macosx-arm64-$release_tag.pom -Dfile=jcef-natives-macosx-arm64-$release_tag.jar -Djavadoc=jcef-natives-macosx-arm64-$release_tag-javadoc.jar -Dsources=jcef-natives-macosx-arm64-$release_tag-sources.jar

mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-windows-amd64-$release_tag.pom -Dfile=jcef-natives-windows-amd64-$release_tag.jar -Djavadoc=jcef-natives-windows-amd64-$release_tag-javadoc.jar -Dsources=jcef-natives-windows-amd64-$release_tag-sources.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-windows-arm64-$release_tag.pom -Dfile=jcef-natives-windows-arm64-$release_tag.jar -Djavadoc=jcef-natives-windows-arm64-$release_tag-javadoc.jar -Dsources=jcef-natives-windows-arm64-$release_tag-sources.jar
mvn gpg:sign-and-deploy-file -Durl=$2 -DrepositoryId=$3 -DpomFile=jcef-natives-windows-i386-$release_tag.pom -Dfile=jcef-natives-windows-i386-$release_tag.jar -Djavadoc=jcef-natives-windows-i386-$release_tag-javadoc.jar -Dsources=jcef-natives-windows-i386-$release_tag-sources.jar

echo "Done uploading maven artifacts!"
