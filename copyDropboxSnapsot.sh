#!/bin/sh

source ./gradle.properties

CLI=restclient-cli/build/libs/restclient-cli-fat-${version}.jar
UI=restclient-ui/build/libs/restclient-ui-fat-${version}.jar
DMG=restclient-ui/build/distributions/restclient-ui-${version}.dmg

cp $CLI $UI ~/Dropbox/Public/

if [ -e $DMG ]; then
  cp $DMG ~/Dropbox/Public/
fi
