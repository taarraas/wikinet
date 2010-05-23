#!/bin/sh

SERVICE_NAME=wordnet2db
JVM_ARGS=-Xmx256m
MAIN_CLASS_NAME=wikinet.wordnet2db.Main

if [ -z "$JDK_HOME" ]; then
    echo ERROR: No JDK found to run wikinet.$SERVICE_NAME. Please validate either JDK_HOME points to valid JDK installation.
fi

SCRIPT_LOCATION=$0
while [ -L "$SCRIPT_LOCATION" ]; do
    SCRIPT_LOCATION=`readlink -e "$SCRIPT_LOCATION"`
done

WIKINET_HOME=`pwd`/`dirname "$SCRIPT_LOCATION"`/..

CLASSPATH=$WIKINET_HOME/lib/wordnet2db-1.0.jar
CLASSPATH=$CLASSPATH:$WIKINET_HOME/lib/common-1.0.jar
CLASSPATH=$CLASSPATH:$WIKINET_HOME/lib/data-tier-1.0.jar
CLASSPATH=$CLASSPATH:$WIKINET_HOME/3rdparty/*

export CLASSPATH

WIKINET_BIN_HOME=`pwd`/`dirname "$SCRIPT_LOCATION"`

cd "$WIKINET_BIN_HOME"
exec $JDK_HOME/bin/java $JVM_ARGS $MAIN_CLASS_NAME $*
