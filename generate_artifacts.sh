#!/bin/bash

if [ ! $# -eq 2 ]
  then
    echo "Usage: ./generate_artifacts.sh <build_meta_url> <mvn_version>"
    echo ""
    echo "build_meta_url: The url to download build_meta.json from"
    echo "mvn_version: The maven version to export to"
    exit 1
fi

export BUILD_META_URL=$1
export MVN_VERSION=$2

#CD to main dir of this repository
cd "$( dirname "$0" )"

#Clean output dir
rm -rf out

#Run docker build
docker-compose --f docker-compose.yml up
