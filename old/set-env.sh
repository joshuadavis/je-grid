#!/bin/bash

DEFAULT_M2_HOME=/opt/apache-maven-3.0.4
DEFAULT_JAVA_HOME=/opt/jdk1.7

cygwin=false;
darwin=false;
case `uname` in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
           fi
           ;;
esac

if [ -z "$M2_HOME" ] ; then
    if [ -d $DEFAULT_M2_HOME ] ; then
        export M2_HOME=$DEFAULT_M2_HOME
    else
        echo "ERROR: Unable to locate Maven. Please set M2_HOME."
        exit -1
    fi
fi

if [ -z "$JAVA_HOME" ] ; then
    export JAVA_HOME=$DEFAULT_JAVA_HOME
fi

if [ ! -d "$JAVA_HOME" ] ; then
    echo "ERROR: JAVA_HOME does not point to a valid directory! $JAVA_HOME"
    exit -1
fi

export PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH

# Cygwin-ify the home directory environment variables.
if [ "$cygwin" = "true" ] ; then
  export M2_HOME=`cygpath --mixed "$M2_HOME"`
  export JAVA_HOME=`cygpath --mixed "$JAVA_HOME"`
fi
