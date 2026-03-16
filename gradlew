#!/bin/sh
#
# Gradle start up script for POSIX
#

# Resolve APP_HOME
app_path=$0
while [ -h "$app_path" ]; do
  ls=$(ls -ld "$app_path")
  link=${ls#*' -> '}
  case $link in
    /*) app_path=$link ;;
    *) app_path=$(dirname "$app_path")/$link ;;
  esac
done
APP_HOME=$(cd "$(dirname "$app_path")" && pwd -P) || exit

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine Java
if [ -n "$JAVA_HOME" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD=java
fi

if ! command -v "$JAVACMD" >/dev/null 2>&1; then
  echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH." >&2
  exit 1
fi

exec "$JAVACMD" \
  -Dfile.encoding=UTF-8 \
  -Xmx64m -Xms64m \
  -Dorg.gradle.appname=gradlew \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
