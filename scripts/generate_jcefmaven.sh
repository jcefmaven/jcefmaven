#!/bin/bash

#CD to base dir of this repository
cd "$( dirname "$0" )" && cd ..

#Clear build dir
rm -rf build
mkdir build
cd build

echo "Creating jcefmaven with tag $mvn_version+$release_tag..."
export platform=\*
export release_download_url=$release_url

#Copy project
cp -r ../jcefmaven .

#Generate pom
rm -f jcefmaven/pom.xml
./../scripts/fill_template.sh jcefmaven/pom.xml.template jcefmaven/pom.xml

#Install required artifacts to local repo
mvn install:install-file -Dfile=/jcefout/jogl-all-$jogl_build.jar -DpomFile=/jcefout/jogl-all-$jogl_build.pom
mvn install:install-file -Dfile=/jcefout/gluegen-rt-$jogl_build.jar -DpomFile=/jcefout/gluegen-rt-$jogl_build.pom
mvn install:install-file -Dfile=/jcefout/jcef-api-$release_tag.jar -DpomFile=/jcefout/jcef-api-$release_tag.pom

#Perform build
cd jcefmaven
mvn clean package source:jar javadoc:jar
cd ..

##########################
#Move built artifacts to export dir
##########################
echo "Exporting artifacts..."
mv jcefmaven/target/jcefmaven-$mvn_version+$release_tag.jar /jcefout
mv jcefmaven/target/jcefmaven-$mvn_version+$release_tag-javadoc.jar /jcefout
mv jcefmaven/target/jcefmaven-$mvn_version+$release_tag-sources.jar /jcefout
mv jcefmaven/pom.xml /jcefout/jcefmaven-$mvn_version+$release_tag.pom

#Done
echo "Done generating api for $mvn_version+$release_tag"
