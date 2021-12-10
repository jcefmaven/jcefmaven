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

echo "Creating maven artifacts for $release_tag..."

#API
echo "###########################################"
echo "# Creating JCEF API for all platforms     #"
echo "###########################################"
./generate_jcef_api.sh

#Macos amd64
echo "###########################################"
echo "# Creating native build for macosx-amd64  #"
echo "###########################################"
./generate_natives.sh macos64 macosx-amd64 $release_tag $download_url_macosx_amd64

#Linux amd64
echo "###########################################"
echo "# Creating native build for linux-amd64   #"
echo "###########################################"
./generate_natives.sh linux64 linux-amd64 $release_tag $download_url_linux_amd64

#Windows amd64
echo "###########################################"
echo "# Creating native build for windows-amd64 #"
echo "###########################################"
./generate_natives.sh win64 windows-amd64 $release_tag $download_url_windows_amd64

