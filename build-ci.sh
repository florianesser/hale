#!/bin/bash

./build/gradlew -p build -Dorg.gradle.jvmargs="-XX:MaxPermSize=512M" --console plain --stacktrace cli -Pargs="$*"
