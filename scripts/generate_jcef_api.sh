#!/bin/bash
set -e

#CD to base dir of this repository
cd "$( dirname "$0" )" && cd ..

#Clear build dir
rm -rf build
mkdir build
cd build

echo "Creating jcef api with tag $release_tag..."
export platform=\*
export release_download_url=$release_url

#Create a download dir
mkdir dl
cd dl

#Fetch artifact
echo "Fetching artifact for $download_url_macosx_amd64..."
curl -s -L -o artifact.tar.gz $download_url_macosx_amd64

#Extract artifact
echo "Extracting..."
tar -zxf artifact.tar.gz
rm artifact.tar.gz

##########################
#Create main API
##########################
cd ..
mkdir api
cd api

#Relocate and prune the files for maven packaging
echo "Building api binaries..."
cp ../dl/bin/jcef_app.app/Contents/Java/jcef.jar jcef.jar
unzip jcef.jar
rm jcef.jar
cp ../dl/LICENSE.txt LICENSE.txt
./../../scripts/fill_template.sh ../../templates/api/README.txt README.txt
./../../scripts/fill_template.sh ../../templates/api/build_meta.json build_meta.json

#Compress contents
echo "Compressing api binaries..."
zip -r jcef-api-$release_tag.jar *

##########################
#Create javadoc
##########################
cd ..
mkdir doc
cd doc

#Relocate and prune the files for maven packaging
echo "Building api javadoc..."
cp -r ../dl/docs/* .
cp ../dl/LICENSE.txt LICENSE.txt
./../../scripts/fill_template.sh ../../templates/api/README.txt README.txt
./../../scripts/fill_template.sh ../../templates/api/build_meta.json build_meta.json

#Compress contents
echo "Compressing api javadoc..."
zip -r jcef-api-$release_tag-javadoc.jar *

##########################
#Create sources
##########################
cd ..
mkdir src
cd src

#Fetch from git, relocate and prune the files for maven packaging
echo "Building api sources..."
git clone $jcef_repository jcef
cd jcef
git checkout $jcef_commit_long
cd ..
cp -r jcef/java/* .
rm -r tests jcef
cp ../dl/LICENSE.txt LICENSE.txt
./../../scripts/fill_template.sh ../../templates/api/README.txt README.txt
./../../scripts/fill_template.sh ../../templates/api/build_meta.json build_meta.json

#Compress contents
echo "Compressing api sources..."
zip -r jcef-api-$release_tag-sources.jar *

##########################
#Generate a pom file
##########################
cd ..
echo "Generating pom..."
./../scripts/fill_template.sh ../templates/api/pom.xml jcef-api-$release_tag.pom

##########################
#Move built artifacts to export dir
##########################
echo "Exporting artifacts..."
mv api/jcef-api-$release_tag.jar /jcefout
mv doc/jcef-api-$release_tag-javadoc.jar /jcefout
mv src/jcef-api-$release_tag-sources.jar /jcefout
mv jcef-api-$release_tag.pom /jcefout

#Done
echo "Done generating api for $release_tag"
