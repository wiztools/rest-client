#!/bin/sh

set -e

# read version:
source ./gradle.properties

jcmd=java
if [ -n "$JAVA_HOME" ]; then
    jcmd=$JAVA_HOME/bin/java
fi

./gradlew build

$jcmd -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog \
  -Dorg.apache.commons.logging.simplelog.log.org.apache.http.wire=DEBUG \
  -Dapple.laf.useScreenMenuBar=true  \
  -Dapple.awt.application.name=RESTClient \
  -jar ui/build/libs/restclient-ui-$version-fat.jar
