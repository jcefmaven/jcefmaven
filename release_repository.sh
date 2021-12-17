#!/usr/bin/env bash
#Script from https://gist.githubusercontent.com/romainbsl/0d0bb2149ce7f34246ec6ab0733a07f1

if [ ! $# -eq 3 ]
  then
    echo "Usage: ./release_repository.sh <user> <password> <stagedRepositoryId>"
    echo ""
    echo "user: Maven Central User"
    echo "password: Maven Central Password"
    echo "stagedRepositoryId: Repository id"
    exit 1
fi

username=$1
password=$2
stagedRepositoryId=$3

if test -z "$username" || test -z "$password" || test -z "$stagedRepositoryId"
then
      echo "Missing parameter(s) for sonatype 'username' | 'password' | 'stagedRepositoryId'."
      exit 1
fi

echo "Closing repository..."

closingRepository=$(
  curl -s --request POST -u "$username:$password" \
    --url https://oss.sonatype.org/service/local/staging/bulk/close \
    --header 'Accept: application/json' \
    --header 'Content-Type: application/json' \
    --data '{ "data" : {"stagedRepositoryIds":["'"$stagedRepositoryId"'"], "description":"Close '"$stagedRepositoryId"'." } }'
)

if [ ! -z "$closingRepository" ]; then
    echo "Error while closing repository $stagedRepositoryId : $closingRepository."
    exit 1
fi

echo "Waiting on close..."

start=$(date +%s)
while true ; do
  # force timeout after 120 minutes
  now=$(date +%s)
  if [ $(( (now - start) / 60 )) -gt 120 ]; then
      echo "Closing process is too long, stopping the job (waiting for closing repository)."
      exit 1
  fi

  rules=$(curl -s --request GET -u "$username:$password" \
        --url https://oss.sonatype.org/service/local/staging/repository/"$stagedRepositoryId"/activity \
        --header 'Accept: application/json' \
        --header 'Content-Type: application/json')

  closingRules=$(echo "$rules" | jq '.[] | select(.name=="close")')
  if [ -z "$closingRules" ] ; then
    continue
  fi

  rulesPassed=$(echo "$closingRules" | jq '.events | any(.name=="rulesPassed")')
  rulesFailed=$(echo "$closingRules" | jq '.events | any(.name=="rulesFailed")')

  if [ "$rulesFailed" = "true" ]; then
    echo "Staged repository [$stagedRepositoryId] could not be closed."
    exit 1
  fi

  if [ "$rulesPassed" = "true" ]; then
      break
  else
      sleep 60
  fi
done

echo "Waiting on transitioning..."

start=$(date +%s)
while true ; do
  # force timeout after 45 minutes
  now=$(date +%s)
  if [ $(( (now - start) / 60 )) -gt 45 ]; then
      echo "Closing process is too long, stopping the job (waiting for transitioning state)."
      exit 1
  fi

  repository=$(curl -s --request GET -u "$username:$password" \
    --url https://oss.sonatype.org/service/local/staging/repository/"$stagedRepositoryId" \
    --header 'Accept: application/json' \
    --header 'Content-Type: application/json')

  type=$(echo "$repository" | jq -r '.type' )
  transitioning=$(echo "$repository" | jq -r '.transitioning' )
  if [ "$type" = "closed" ] && [ "$transitioning" = "false" ]; then
      break
  else
      sleep 60
  fi
done

echo "Releasing repo..."

release=$(curl -s --request POST -u "$username:$password" \
  --url https://oss.sonatype.org/service/local/staging/bulk/promote \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '{ "data" : {"stagedRepositoryIds":["'"$stagedRepositoryId"'"], "autoDropAfterRelease" : true, "description":"Release '"$stagedRepositoryId"'." } }')

if [ ! -z "$release" ]; then
    echo "Error while releasing $stagedRepositoryId : $release."
    exit 1
fi

echo "Done releasing $3."

