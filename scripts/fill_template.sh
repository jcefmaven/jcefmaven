#!/bin/bash

if [ ! $# -eq 4 ]
  then
    echo "Usage: ./fill_template.sh <template> <dest> <platform> <release_tag>"
    echo ""
    echo "template: the template file"
    echo "dest: the destination file"
    echo "platform: name of the platform to release for (e.g. linux-i386)"
    echo "release_tag: the tag of the release (jcef+X+cef+Y)"
    exit 1
fi

#Read file, replace and write to target
sed "s|{platform}|$3|;s|{release_tag}|$4|" $1 > $2
