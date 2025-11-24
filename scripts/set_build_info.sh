#!/bin/bash
set -e

if [ ! $# -eq 2 ]
  then
    echo "Usage: ./set_build_info.sh <build_meta_url> <mvn_version>"
    echo ""
    echo "build_meta_url: The url to download build_meta.json from"
    echo "mvn_version: The maven version to export to"
    exit 1
fi

#Print build meta location
echo "Initializing for build from $1 for $2..."

#Download build_meta.json and import to local environment
export $(curl -s -L $1 | jq -r "to_entries|map(\"\(.key)=\(.value|tostring)\")|.[]")

#Set JOGL information
export jogl_build=v2.4.0
export jogl_download=https://jogamp.org/deployment/$jogl_build/jar #Without terminating /!
export jogl_git=https://jogamp.org/cgit/jogl.git
export jogl_commit=e55af768993843ad9c782248252bf995e4f6ce99 #From META-INF
export gluegen_git=https://jogamp.org/cgit/gluegen.git
export gluegen_commit=3a68c5012e0e536639a2aa753eee180834421c46 #From META-INF

#Set jcefmaven information
export mvn_version=$2

