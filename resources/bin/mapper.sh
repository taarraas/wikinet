#!/bin/sh

SERVICE_NAME=mapper
MAIN_CLASS_NAME=wikinet.mapping.Main

if [ -z "$JDK_HOME" ]; then
    echo ERROR: No JDK found to run wikinet.$SERVICE_NAME. Please validate either JDK_HOME points to valid JDK installation.
fi

SCRIPT_LOCATION=$0
while [ -L "$SCRIPT_LOCATION" ]; do
    SCRIPT_LOCATION=`readlink -e "$SCRIPT_LOCATION"`
done

WIKINET_HOME=`pwd`/`dirname "$SCRIPT_LOCATION"`/..

JVM_ARGS=-Xmx256m -Djms.properties.file=$WIKINET_HOME/conf/jms.properties

CLASSPATH=$WIKINET_HOME/lib/mapper-1.0.jar
CLASSPATH=$CLASSPATH:$WIKINET_HOME/lib/wiki-1.0.jar
CLASSPATH=$CLASSPATH:$WIKINET_HOME/lib/data-tier-1.0.jar
CLASSPATH=$CLASSPATH:$WIKINET_HOME/lib/jms-1.0.jar
CLASSPATH=$CLASSPATH:$WIKINET_HOME/3rdparty/*

export CLASSPATH

WIKINET_BIN_HOME=`pwd`/`dirname "$SCRIPT_LOCATION"`

cd "$WIKINET_BIN_HOME"
exec $JDK_HOME/bin/java $JVM_ARGS $MAIN_CLASS_NAME $*
