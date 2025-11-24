#!/bin/bash
set -e

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
set +e
unzip '*.jar'
rm '*.jar'
set -e

#Remove meta-inf as it contains wrong hashes
rm -r META-INF

#Compress contents
echo "Compressing package..."
zip -r "$1-$jogl_build.jar" *

#Generate a pom file
echo "Generating pom..."
./../scripts/fill_template.sh "../templates/$1/pom.xml" "$1-$jogl_build.pom"

#Build sources
if [[ "$1" == "jogl-all" ]] ; then
   while ! (git clone "$jogl_git" sources && cd sources && git checkout "$jogl_commit") || (( count++ >= 5))
   do
     echo "Failed cloning sources, retrying..."
     cd ..
     rm -rf sources
   done
   cd ..
   mkdir exp
   # Merge Sources
   cp -r sources/src/jogl/classes/* exp
   cp -r sources/src/newt/classes/* exp
   cp -r sources/src/nativewindow/classes/* exp
   cd exp
else
   while ! (git clone "$gluegen_git" sources && cd sources && git checkout "$gluegen_commit") || (( count++ >= 5))
   do
     echo "Failed cloning sources, retrying..."
     rm -rf sources
   done
   cd ..
   mkdir exp
   cp -r sources/src/java/* exp
   cd exp
   # Prune files
   cd com/jogamp/gluegen && rm -rf *.java *.html ant *gram pcpp procaddress structgen && cd ../../..
   cd jogamp && rm -rf android && cd ..
   rm -rf net
fi
zip -r ../$1-$jogl_build-sources.jar *
cd ..
rm -rf exp sources

#Build javadoc
mkdir javadoc
cd javadoc
if [[ "$1" == "jogl-all" ]] ; then
    curl -s -L -o javadoc.7z $jogl_download/../archive/jogl-javadoc.7z
    7z x javadoc.7z
    cd jogl/javadoc
    zip -r ../../../$1-$jogl_build-javadoc.jar *
else
    curl -s -L -o javadoc.7z $jogl_download/../archive/gluegen-javadoc.7z
    7z x javadoc.7z
    cd gluegen/javadoc
    zip -r ../../../$1-$jogl_build-javadoc.jar *
fi
cd ../../..
rm -rf javadoc

#Move built artifacts to export dir
echo "Exporting artifacts..."
mv $1-$jogl_build.jar /jcefout
mv $1-$jogl_build-sources.jar /jcefout
mv $1-$jogl_build-javadoc.jar /jcefout
mv $1-$jogl_build.pom /jcefout

#Done
echo "Done generating $1 with version $jogl_build"
