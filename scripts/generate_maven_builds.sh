#!/bin/bash

if [ ! $# -eq 1 ]
  then
    echo "Usage: ./generate_maven_builds.sh <build_meta_url>"
    echo ""
    echo "build_meta_url: The url to download build_meta.json from"
    exit 1
fi

#Download build_meta.json and import to local environment
export $(curl -s -L $1 | jq -r "to_entries|map(\"\(.key)=\(.value|tostring)\")|.[]")

echo "Creating maven artifacts for $release_tag..."
