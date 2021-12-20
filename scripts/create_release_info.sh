#!/bin/bash

if [ ! $# -eq 3 ]
  then
    echo "Usage: ./create_release_info.sh <build_meta> <actionsurl> <actionsrunnumber>"
    echo ""
    echo "build_meta: the url to fetch the build meta from"
    echo "actionsurl: the url pointing to the builder job"
    echo "actionsrunnumber: the number of the current build"
    exit 1
fi

#CD to release_info dir
cd "$( dirname "$0" )"

#Set build info
. set_build_info.sh $1 $3

#Create release_info dir and cd to it
cd ..
rm -r -f release_info
mkdir release_info
cd release_info

#Pull from git
git clone $jcef_repository jcef
cd jcef
git checkout $jcef_commit_long

#Dump git commit message
commit_message=$(git log -1 --pretty=%B)

#Build final release information
#Tag
release_tag=$(echo "$mvn_version+jcef-$jcef_commit+cef-$cef_version" | awk '{print}' ORS='')

echo "release_tag_name=$tag_version.$3" >> $GITHUB_ENV

#Name
echo "release_name=MVN $mvn_version + JCEF $jcef_commit + CEF $cef_version" >> $GITHUB_ENV 

#Readme
(
  echo "**Update JCEF to [$jcef_commit]($jcef_url)**"
  echo ""
  echo "Build: [GitHub Actions #$3]($2)"
  echo "MVN version: $mvn_version"
  echo "JCEF version: $jcef_commit"
  echo "CEF version: $cef_version"
  echo ""
  echo "**Use with Maven:**"
  echo "\`\`\`"
  echo "<dependency>"
  echo "    <groupId>me.friwi</groupId>"
  echo "    <artifactId>jcefmaven</artifactId>"
  echo "    <version>$release_tag</version>"
  echo "</dependency>"
  echo "\`\`\`"
  echo ""
  echo "**Use with Gradle:**"
  echo "\`\`\`"
  echo "implementation 'me.friwi:jcefmaven:$release_tag'"
  echo "\`\`\`"
  echo ""
  echo "**Changes from previous release:**"
  echo "\`\`\`"
  echo "$commit_message"
  echo "\`\`\`"
  echo "**Test results of this release**"
  echo "These test results are provided by developers like yourself. If the platform you are currently running on is marked as \`untested\` below, please consider submitting a test report. You can report your test results using a [new issue](https://github.com/jcefmaven/jcefmaven/issues/new/choose) in just a minute! It will help other developers in choosing a stable version. Thank you!"
  echo "|  | <a href="#"><img src=\"https://simpleicons.org/icons/linux.svg\" alt=\"linux\" width=\"32\" height=\"32\"></a> | <a href="#"><img src=\"https://simpleicons.org/icons/windows.svg\" alt=\"windows\" width=\"32\" height=\"32\"></a> | <a href="#"><img src=\"https://simpleicons.org/icons/apple.svg\" alt=\"macosx\" width=\"32\" height=\"32\"></a> |"
  echo "|---|---|---|---|"
  echo "|amd64|[![Untested](https://img.shields.io/badge/linux--amd64-Untested-lightgrey)](#)|[![Untested](https://img.shields.io/badge/windows--amd64-Untested-lightgrey)](#)|[![Untested](https://img.shields.io/badge/macosx--amd64-Untested-lightgrey)](#)|"
  echo "|arm64|[![Untested](https://img.shields.io/badge/linux--arm64-Untested-lightgrey)](#)|[![Untested](https://img.shields.io/badge/windows--arm64-Untested-lightgrey)](#)|[![Untested](https://img.shields.io/badge/macosx--arm64-Untested-lightgrey)](#)|"
  echo "|i386|[![Untested](https://img.shields.io/badge/linux--i386-Untested-lightgrey)](#)|[![Untested](https://img.shields.io/badge/windows--i386-Untested-lightgrey)](#)| - |"
  echo "|arm|[![Untested](https://img.shields.io/badge/linux--arm-Untested-lightgrey)](#)| - | - |"
) > ../release_message.md

#Add build_meta.json
curl -s -L -o ../build_meta.json $1

#Cleanup
cd ..
rm -rf jcef
