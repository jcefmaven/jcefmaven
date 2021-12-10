#!/bin/bash

if [ ! $# -eq 4 ]
  then
    echo "Usage: ./generate_linux.sh <bit> <platform> <release_tag> <artifact_url>"
    echo ""
    echo "bit: 32 or 64 (for linux32 and linux64)"
    echo "platform: name of the platform to release for (e.g. linux-i386)"
    echo "release_tag: the tag of the release (jcef+X+cef+Y)"
    echo "artifact_url: URL to download artifact from"
    exit 1
fi

#Move to base dir of this repository
cd "$( dirname "$0" )" && cd ..

#Clear build dir
rm -rf build
mkdir build
cd build

echo "Creating a linux build for $2 with tag $3..."

#Fetch artifact
echo "Fetching $2"
curl -L -o linux.tar.gz $4

#Extract artifact
echo "Extracting..."
tar -zxf linux.tar.gz
rm linux.tar.gz

#Relocate and prune the files for maven packaging
echo "Building package..."
rm -f compile.sh gluegen.LICENSE.txt jogl.LICENSE.txt README.txt run.sh
mv bin/lib/linux$1/* .
mv bin/jogl*.jar .
mv bin/gluegen*.jar .
rm -rf bin

#Generate a readme file
./../fill_template.sh ../natives_README.txt README.txt $2 $3

#Compress contents
echo "Compressing package..."
tar -zcf jcef-natives-$2-$3.tar.gz *
zip jcef-natives-$2-$3.jar jcef-natives-$2-$3.tar.gz

#Generate a pom file
echo "Generating pom..."
./../scripts/fill_template.sh ../templates/natives_pom.xml jcef-natives-$2-$3.pom $2 $3

#Move built artifacts to export dir
echo "Exporting artifacts..."
mv jcef-natives-$2-$3.jar ../out
mv jcef-natives-$2-$3.pom ../out

#Done
echo "Done generating natives for $2-$3"
