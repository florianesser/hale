#!/bin/bash

./build/gradlew -p build -Dorg.gradle.jvmargs="-XX:MaxPermSize=512M" -Dorg.gradle.daemon=false --console plain --stacktrace cli -Pargs="$*"
