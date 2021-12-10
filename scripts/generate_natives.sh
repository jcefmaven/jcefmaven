#!/bin/bash

if [ ! $# -eq 4 ]
  then
    echo "Usage: ./generate_natives.sh <bit> <platform> <release_tag> <artifact_url>"
    echo ""
    echo "bit: linux32, linux64, macos64, win32 or win64"
    echo "platform: name of the platform to release for (e.g. linux-i386)"
    echo "release_tag: the tag of the release (jcef+X+cef+Y)"
    echo "artifact_url: URL to download artifact from"
    exit 1
fi

#CD to base dir of this repository
cd "$( dirname "$0" )" && cd ..

#Clear build dir
rm -rf build
mkdir build
cd build

echo "Creating natives for $2 with tag $3..."
export platform=$2
export release_download_url=$4

#Fetch artifact
echo "Fetching artifact for $2..."
curl -s -L -o artifact.tar.gz $4

#Extract artifact
echo "Extracting..."
tar -zxf artifact.tar.gz
rm artifact.tar.gz

#Relocate and prune the files for maven packaging
echo "Building package..."
rm -f compile.sh compile.bat README.txt run.sh run.bat
if [ "$1" == "macos64" ] ; then
  mv bin/jcef_app.app/Contents/Frameworks/* .
  mv bin/jcef_app.app/Contents/Java/jogl*.jar .
  mv bin/jcef_app.app/Contents/Java/gluegen*.jar .
  mv bin/jcef_app.app/Contents/Java/libjcef.dylib .
else
  mv bin/lib/$1/* .
  mv bin/jogl*.jar .
  mv bin/gluegen*.jar .
fi
rm -rf bin docs tests

#Generate a readme file
./../scripts/fill_template.sh ../templates/natives/README.txt README.txt

#Generate a build_meta file
./../scripts/fill_template.sh ../templates/natives/build_meta.json build_meta.json

#Compress contents
echo "Compressing package..."
tar -zcf jcef-natives-$2-$3.tar.gz *
zip jcef-natives-$2-$3.jar jcef-natives-$2-$3.tar.gz

#Generate a pom file
echo "Generating pom..."
./../scripts/fill_template.sh ../templates/natives/pom.xml jcef-natives-$2-$3.pom

#Move built artifacts to export dir
echo "Exporting artifacts..."
mv jcef-natives-$2-$3.jar /jcefout
mv jcef-natives-$2-$3.pom /jcefout

#Done
echo "Done generating natives for $2-$3"
