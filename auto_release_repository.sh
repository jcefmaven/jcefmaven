#!/bin/bash

if [ ! $# -eq 2 ]
  then
    echo "Usage: ./auto_release_repository.sh <user> <password>"
    echo ""
    echo "user: Maven Central User"
    echo "password: Maven Central Password"
    exit 1
fi

#CD to dir of this script
cd "$( dirname "$0" )"

#List repos
echo "Fetching repositories..."
repolist=$(curl -s -u "$1:$2" 'https://oss.sonatype.org/service/local/staging/profile_repositories?_dc=1639716203993' -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:95.0) Gecko/20100101 Firefox/95.0' -H 'Accept: application/json,application/vnd.siesta-error-v1+json,application/vnd.siesta-validation-errors-v1+json' -H 'Accept-Language: en-US,en;q=0.5' -H 'Accept-Encoding: gzip, deflate, br' -H 'X-Nexus-UI: true' -H 'X-Requested-With: XMLHttpRequest' -H 'Connection: keep-alive' -H 'Referer: https://oss.sonatype.org/' -H 'Sec-Fetch-Dest: empty' -H 'Sec-Fetch-Mode: cors' -H 'Sec-Fetch-Site: same-origin' -H 'TE: trailers')
echo "$repolist"
echo ""

repolist=$(sed 's|\"|\n|g' <<< $repolist)

#Release all open repos
echo "Releasing newest repo..."
while IFS= read -r repo; do
    if [[ $repo == mefriwi* ]] ; then
      release=$repo
    fi
done <<< "$repolist"

if test -z "$release"
then
  echo "No repository to release found!"
  exit 1
fi

echo "Releasing $release..."
./release_repository.sh $1 $2 $release
