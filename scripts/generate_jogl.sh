#!/bin/bash

if [ ! $# -eq 1 ]
  then
    echo "Usage: ./generate_jogl.sh <artifact>"
    echo ""
    echo "artifact: the artifact to create (e.g. jogl-all or gluegen-rt)"
    exit 1
fi

#CD to base dir of this repository
cd "$( dirname "$0" )" && cd ..

#Clear build dir
rm -rf build
mkdir build
cd build

echo "Creating $1 with version $jogl_build..."
export platform=*
export release_download_url=$jogl_download

#Fetch artifact
echo "Fetching artifacts..."
curl -s -L -o $1.jar $jogl_download/$1.jar
curl -s -L -o $1-natives-linux-aarch64.jar $jogl_download/$1-natives-linux-aarch64.jar
curl -s -L -o $1-natives-linux-amd64.jar $jogl_download/$1-natives-linux-amd64.jar
curl -s -L -o $1-natives-linux-armv6hf.jar $jogl_download/$1-natives-linux-armv6hf.jar
curl -s -L -o $1-natives-linux-i586.jar $jogl_download/$1-natives-linux-i586.jar
curl -s -L -o $1-natives-macosx-universal.jar $jogl_download/$1-natives-macosx-universal.jar
curl -s -L -o $1-natives-windows-amd64.jar $jogl_download/$1-natives-windows-amd64.jar
curl -s -L -o $1-natives-windows-i586.jar $jogl_download/$1-natives-windows-i586.jar

#Extract artifacts
echo "Extracting..."
unzip '*.jar'
rm *.jar

#Remove meta-inf as it contains wrong hashes
rm -r META-INF

#Compress contents
echo "Compressing package..."
zip -r $1-$jogl_build.jar *

#Generate a pom file
echo "Generating pom..."
./../scripts/fill_template.sh ../templates/$1/pom.xml $1-$jogl_build.pom

#Move built artifacts to export dir
echo "Exporting artifacts..."
mv $1-$jogl_build.jar /jcefout
mv $1-$jogl_build.pom /jcefout

#Done
echo "Done generating $1 with version $jogl_build"
