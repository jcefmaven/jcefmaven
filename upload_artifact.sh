#!/bin/bash
set -e

if [ ! $# -eq 3 ]
  then
    echo "Usage: ./upload_artifact.sh <groupId> <artifactId> <version>"
    echo "Release repo url should NOT end in /!"
    exit 1
fi

groupId=$1
artifactId=$2
version=$3
pathGroupId=$(sed 's|\.|\/|g' <<< $groupId)

#CD to the upload dir
cd "$( dirname "$0" )" && cd upload

echo "Uploading $artifactId-$version..."
rm -rf groupId
rm -f central-bundle.zip
mkdirs groupId/artifactId/version
for file in "$artifactId-$version"*
do
  mv "$file" groupId/artifactId/version
done

zip -r central-bundle.zip me

curl --request POST \
  --verbose \
  --header "Authorization: Bearer $(echo "$MAVEN_USERNAME:$MAVEN_CENTRAL_TOKEN" | base64)" \
  --form bundle=@central-bundle.zip \
  -d "publishingType=AUTOMATIC" \
  https://central.sonatype.com/api/v1/publisher/upload

rm -rf me
rm central-bundle.zip
