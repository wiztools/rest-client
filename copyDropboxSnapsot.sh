#!/bin/sh

VERSION=`head pom.xml | grep 'version.*version' | sed -E 's/.*version.(.*)..version.*/\1/g'`

cp restclient-cli/target/restclient-cli-${VERSION}-jar-with-dependencies.jar \
  restclient-ui/target/restclient-ui-${VERSION}-jar-with-dependencies.jar \
  ~/Dropbox/Public/

if [ -e restclient-ui/target/RESTClient-${VERSION}.dmg ]; then
  cp restclient-ui/target/RESTClient-${VERSION}.dmg ~/Dropbox/Public/
fi

