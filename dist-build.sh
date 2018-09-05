#!/bin/sh

./gradlew clean build fatJar createApp createAppZip createDmg
./gradlew assembleDist -p restclient-ui
