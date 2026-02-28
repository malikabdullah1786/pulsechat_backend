#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Maven2 Start Up Batch script
#
# Required ENV vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars:
# ------------------
#   MAVEN_HOME - location of maven2's installed home dir
#   MAVEN_OPTS - parameters passed to the Java VM when running Maven
#     e.g. to debug Maven itself, use
#       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
# ----------------------------------------------------------------------------

CORE_DIR=`pwd`
# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false;
darwin=false;
os400=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true ;;
  OS400*) os400=true ;;
esac

if [ -z "$JAVA_HOME" ] ; then
  if [ -r /etc/gentoo-release ] ; then
    JAVA_HOME=`java-config --jre-home`
  fi
fi

if [ -z "$JAVA_HOME" ] ; then
  if [ -n "$JAVA_PATH" ]; then
    JAVA_HOME=`dirname "$JAVA_PATH"`
    JAVA_HOME=`dirname "$JAVA_HOME"`
  fi
fi

if [ -z "$JAVA_HOME" ] ; then
  if [ -x /usr/bin/java ]; then
    JAVA_HOME=`/usr/bin/java -XshowSettings:properties -version 2>&1 > /dev/null | grep 'java.home' | awk '{print $3}'`
  fi
fi

if [ -z "$JAVA_HOME" ] ; then
  warn "JAVA_HOME is not set and no 'java' command could be found in your PATH."
  warn "Please set JAVA_HOME or add the 'java' executable to your PATH."
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For OS400
if $os400; then
  # Support for 80-column terminals used in AS/400
  export MAVEN_OPTS="$MAVEN_OPTS -Dcom.ibm.midp.character_encoding=ISO8859_1"
fi

# If MAVEN_HOME is not set, use the directory of this script.
if [ -z "$MAVEN_HOME" ]; then
  MAVEN_HOME=`dirname "$0"`
  MAVEN_HOME=`cd "$MAVEN_HOME" && pwd`
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
  [ -n "$MAVEN_HOME" ] && MAVEN_HOME=`cygpath --windows "$MAVEN_HOME"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

# EXECUTION
exec "$JAVA_HOME/bin/java" $MAVEN_OPTS -classpath "$MAVEN_HOME/boot/plexus-classworlds-*.jar" "-Dclassworlds.conf=$MAVEN_HOME/bin/m2.conf" "-Dmaven.home=$MAVEN_HOME" "-Dmaven.multiModuleProjectDirectory=$CORE_DIR" org.codehaus.plexus.classworlds.launcher.Launcher "$@"
