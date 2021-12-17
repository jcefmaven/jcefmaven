#!/bin/bash

if [ ! $# -eq 2 ]
  then
    echo "Usage: ./close_repository.sh <user> <password>"
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
repos=$(echo '$repolist' | grep -oP '(?<=repositoryId\":\").*?(?=\",\"type\":\"open)')
echo "$repolist"
echo ""
echo "Repositories: "
echo "$repos"
echo ""

#Close all open repos
echo "Closing all open repos..."
while IFS= read -r repo; do
    if [[ "$repo" == '' ]]; then
      break
    fi
    echo "Closing $repo"
    curl -s -u "$1:$2" 'https://oss.sonatype.org/service/local/staging/bulk/close' -X POST -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:95.0) Gecko/20100101 Firefox/95.0' -H 'Accept: application/json,application/vnd.siesta-error-v1+json,application/vnd.siesta-validation-errors-v1+json' -H 'Accept-Language: en-US,en;q=0.5' -H 'Accept-Encoding: gzip, deflate, br' -H 'X-Nexus-UI: true' -H 'Content-Type: application/json' -H 'X-Requested-With: XMLHttpRequest' -H 'Origin: https://oss.sonatype.org' -H 'Connection: keep-alive' -H 'Referer: https://oss.sonatype.org/' -H 'Sec-Fetch-Dest: empty' -H 'Sec-Fetch-Mode: cors' -H 'Sec-Fetch-Site: same-origin' -H 'TE: trailers' --data-raw $'{"data":{"description":"Closing $repo","stagedRepositoryIds":["$repo"]}}'
done <<< "$repos"

echo "All open repos closed!"
