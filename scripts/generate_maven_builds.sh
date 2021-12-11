#!/bin/bash

if [ ! $# -eq 1 ]
  then
    echo "Usage: ./generate_maven_builds.sh <build_meta_url>"
    echo ""
    echo "build_meta_url: The url to download build_meta.json from"
    exit 1
fi

#CD to dir of this script
cd "$( dirname "$0" )"

#Print build meta location
echo "Initializing for build from $1..."

#Clear export dir
rm -rf /jcefout/*

#Download build_meta.json and import to local environment
export $(curl -s -L $1 | jq -r "to_entries|map(\"\(.key)=\(.value|tostring)\")|.[]")

#Set JOGL information (also set in upload_artifacts.sh!)
export jogl_build=v2.4.0-rc-20210111
export jogl_download=https://jogamp.org/deployment/$jogl_build/jar #Without terminating /!

echo "Creating maven artifacts for $release_tag..."

#jogl-all
echo "###########################################"
echo "# Creating jogl for all platforms         #"
echo "###########################################"
./generate_jogl.sh jogl-all

#gluegen-rt
echo "###########################################"
echo "# Creating gluegen-rt for all platforms   #"
echo "###########################################"
./generate_jogl.sh gluegen-rt

#API
echo "###########################################"
echo "# Creating JCEF API for all platforms     #"
echo "###########################################"
./generate_jcef_api.sh

#jcefmaven
echo "###########################################"
echo "# Creating jcefmaven for all platforms    #"
echo "###########################################"
./generate_jcefmaven.sh

#Linux amd64
echo "###########################################"
echo "# Creating native build for linux-amd64   #"
echo "###########################################"
./generate_natives.sh linux64 linux-amd64 $release_tag $download_url_linux_amd64

#Linux arm64
echo "###########################################"
echo "# Creating native build for linux-arm64   #"
echo "###########################################"
./generate_natives.sh linux64 linux-arm64 $release_tag $download_url_linux_arm64

#Linux i386
echo "###########################################"
echo "# Creating native build for linux-i386    #"
echo "###########################################"
./generate_natives.sh linux32 linux-i386 $release_tag $download_url_linux_i386

#Linux arm
echo "###########################################"
echo "# Creating native build for linux-arm     #"
echo "###########################################"
./generate_natives.sh linux32 linux-arm $release_tag $download_url_linux_arm

#Macos amd64
echo "###########################################"
echo "# Creating native build for macosx-amd64  #"
echo "###########################################"
./generate_natives.sh macos64 macosx-amd64 $release_tag $download_url_macosx_amd64

#Macos arm64
echo "###########################################"
echo "# Creating native build for macosx-arm64  #"
echo "###########################################"
./generate_natives.sh macos64 macosx-arm64 $release_tag $download_url_macosx_arm64

#Windows amd64
echo "###########################################"
echo "# Creating native build for windows-amd64 #"
echo "###########################################"
./generate_natives.sh win64 windows-amd64 $release_tag $download_url_windows_amd64

#Windows arm64
echo "###########################################"
echo "# Creating native build for windows-arm64 #"
echo "###########################################"
./generate_natives.sh win64 windows-arm64 $release_tag $download_url_windows_arm64

#Windows i386
echo "###########################################"
echo "# Creating native build for windows-i386  #"
echo "###########################################"
./generate_natives.sh win32 windows-i386 $release_tag $download_url_windows_i386

