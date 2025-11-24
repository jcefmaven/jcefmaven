#!/bin/bash

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
export jogl_commit=7982cc52344c025c40da45fd4b946056a63bc855 #From META-INF
export gluegen_git=https://jogamp.org/cgit/gluegen.git
export gluegen_commit=9dce06050a8a607b8c4ab83bd3aba8460d9ca593 #From META-INF

#Set jcefmaven information
export mvn_version=$2

