#!/bin/bash

if [ ! $# -eq 2 ]
  then
    echo "Usage: ./update_readme.sh <build_meta_url> <mvn_version>"
    echo ""
    echo "build_meta_url: The url to download build_meta.json from"
    echo "mvn_version: The maven version to export to"
    exit 1
fi

#CD to dir of this script
cd "$( dirname "$0" )"

#Set build info
. scripts/set_build_info.sh $1 $2

#Update readme
./scripts/fill_template.sh README.template.md README.md

#Push to git
git add README.md
git config user.name github-actions
git config user.email github-actions@github.com
#Allow for no update of version number
set +e
git commit -m "Update README.md to $mvn_version"
git push -u origin master
