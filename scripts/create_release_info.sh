#!/bin/bash

if [ ! $# -eq 4 ]
  then
    echo "Usage: ./create_release_info.sh <build_meta> <actionsurl> <actionsrunnumber> <mvn_version>"
    echo ""
    echo "build_meta: the url to fetch the build meta from"
    echo "actionsurl: the url pointing to the builder job"
    echo "actionsrunnumber: the number of the current build"
    echo "mvn_version: The maven version to export to"
    exit 1
fi

#CD to release_info dir
cd "$( dirname "$0" )"

#Set build info
. set_build_info.sh $1 $4

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
echo "release_tag_name=$4" >> $GITHUB_ENV

#Name
echo "release_name=JCEF Maven $mvn_version" >> $GITHUB_ENV 

#Readme
(
  echo "**Update JCEF to [$jcef_commit]($jcef_url)**"
  echo ""
  echo "Build: [GitHub Actions #$3]($2)"
  echo "MVN version: $mvn_version"
  echo "JCEF commit: $jcef_commit"
  echo "CEF version: $cef_version"
  echo ""
  echo "**Use with Maven:**"
  echo "\`\`\`"
  echo "<dependency>"
  echo "    <groupId>me.friwi</groupId>"
  echo "    <artifactId>jcefmaven</artifactId>"
  echo "    <version>$mvn_version</version>"
  echo "</dependency>"
  echo "\`\`\`"
  echo ""
  echo "<details>"
  echo "<summary>Natives (only include if you want to bundle them in a fat jar)</summary>"
  echo ""
  echo "##### Linux AMD64"
  echo "\`\`\`"
  echo "<dependency>"
  echo "    <groupId>me.friwi</groupId>"
  echo "    <artifactId>jcef-natives-linux-amd64</artifactId>"
  echo "    <version>jcef-$jcef_commit+cef-$cef_version</version>"
  echo "</dependency>"
  echo "\`\`\`"
  echo ""
  echo "##### Linux ARM"
  echo "\`\`\`"
  echo "<dependency>"
  echo "    <groupId>me.friwi</groupId>"
  echo "    <artifactId>jcef-natives-linux-arm</artifactId>"
  echo "    <version>jcef-$jcef_commit+cef-$cef_version</version>"
  echo "</dependency>"
  echo "\`\`\`"
  echo ""
  echo "##### Linux ARM64"
  echo "\`\`\`"
  echo "<dependency>"
  echo "    <groupId>me.friwi</groupId>"
  echo "    <artifactId>jcef-natives-linux-arm64</artifactId>"
  echo "    <version>jcef-$jcef_commit+cef-$cef_version</version>"
  echo "</dependency>"
  echo "\`\`\`"
  echo ""
  echo "##### Macosx AMD64"
  echo "\`\`\`"
  echo "<dependency>"
  echo "    <groupId>me.friwi</groupId>"
  echo "    <artifactId>jcef-natives-macosx-amd64</artifactId>"
  echo "    <version>jcef-$jcef_commit+cef-$cef_version</version>"
  echo "</dependency>"
  echo "\`\`\`"
  echo ""
  echo "##### Macosx ARM64"
  echo "\`\`\`"
  echo "<dependency>"
  echo "    <groupId>me.friwi</groupId>"
  echo "    <artifactId>jcef-natives-macosx-arm64</artifactId>"
  echo "    <version>jcef-$jcef_commit+cef-$cef_version</version>"
  echo "</dependency>"
  echo "\`\`\`"
  echo ""
  echo "##### Windows AMD64"
  echo "\`\`\`"
  echo "<dependency>"
  echo "    <groupId>me.friwi</groupId>"
  echo "    <artifactId>jcef-natives-windows-amd64</artifactId>"
  echo "    <version>jcef-$jcef_commit+cef-$cef_version</version>"
  echo "</dependency>"
  echo "\`\`\`"
  echo ""
  echo "##### Windows ARM64"
  echo "\`\`\`"
  echo "<dependency>"
  echo "    <groupId>me.friwi</groupId>"
  echo "    <artifactId>jcef-natives-windows-arm64</artifactId>"
  echo "    <version>jcef-$jcef_commit+cef-$cef_version</version>"
  echo "</dependency>"
  echo "\`\`\`"
  echo ""
  echo "##### Windows i386"
  echo "\`\`\`"
  echo "<dependency>"
  echo "    <groupId>me.friwi</groupId>"
  echo "    <artifactId>jcef-natives-windows-i386</artifactId>"
  echo "    <version>jcef-$jcef_commit+cef-$cef_version</version>"
  echo "</dependency>"
  echo "\`\`\`"
  echo "</details>"
  echo ""
  echo "**Use with Gradle:**"
  echo "\`\`\`"
  echo "implementation 'me.friwi:jcefmaven:$mvn_version'"
  echo "\`\`\`"
  echo ""
  echo "<details>"
  echo "<summary>Natives (only include if you want to bundle them in a fat jar)</summary>"
  echo ""
  echo "##### Linux AMD64"
  echo "\`\`\`"
  echo "implementation 'me.friwi:jcef-natives-linux-amd64:jcef-$jcef_commit+cef-$cef_version'"
  echo "\`\`\`"
  echo ""
  echo "##### Linux ARM"
  echo "\`\`\`"
  echo "implementation 'me.friwi:jcef-natives-linux-arm:jcef-$jcef_commit+cef-$cef_version'"
  echo "\`\`\`"
  echo ""
  echo "##### Linux ARM64"
  echo "\`\`\`"
  echo "implementation 'me.friwi:jcef-natives-linux-arm64:jcef-$jcef_commit+cef-$cef_version'"
  echo "\`\`\`"
  echo ""
  echo "##### Macosx AMD64"
  echo "\`\`\`"
  echo "implementation 'me.friwi:jcef-natives-macosx-amd64:jcef-$jcef_commit+cef-$cef_version'"
  echo "\`\`\`"
  echo ""
  echo "##### Macosx ARM64"
  echo "\`\`\`"
  echo "implementation 'me.friwi:jcef-natives-macosx-arm64:jcef-$jcef_commit+cef-$cef_version'"
  echo "\`\`\`"
  echo ""
  echo "##### Windows AMD64"
  echo "\`\`\`"
  echo "implementation 'me.friwi:jcef-natives-windows-amd64:jcef-$jcef_commit+cef-$cef_version'"
  echo "\`\`\`"
  echo ""
  echo "##### Windows ARM64"
  echo "\`\`\`"
  echo "implementation 'me.friwi:jcef-natives-windows-arm64:jcef-$jcef_commit+cef-$cef_version'"
  echo "\`\`\`"
  echo ""
  echo "##### Windows i386"
  echo "\`\`\`"
  echo "implementation 'me.friwi:jcef-natives-windows-i386:jcef-$jcef_commit+cef-$cef_version'"
  echo "\`\`\`"
  echo "</details>"
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
  echo "|i386| - |[![Untested](https://img.shields.io/badge/windows--i386-Untested-lightgrey)](#)| - |"
  echo "|arm|[![Untested](https://img.shields.io/badge/linux--arm-Untested-lightgrey)](#)| - | - |"
) > ../release_message.md

#Add build_meta.json
curl -s -L -o ../build_meta.json $1

#Cleanup
cd ..
rm -rf jcef
