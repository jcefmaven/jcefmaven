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
export jogl_commit=ecf6e499d3b582d651a28693c871ca14d6e8c991 #From META-INF
export gluegen_git=https://jogamp.org/cgit/gluegen.git
export gluegen_commit=0b441cfc14947b1c8cabdc87705ae95a0afec4d9 #From META-INF

#Set jcefmaven information
export mvn_version=$2

