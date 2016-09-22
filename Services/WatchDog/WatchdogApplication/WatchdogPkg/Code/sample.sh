#!/bin/bash
#-Xdebug -Xnoagent -Djava.compiler=NONE  -Xrunjdwp:transport=dt_socket,server=y,address=8888,suspend=n
BASEDIR=$(dirname $0)
cd $BASEDIR
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$(pwd)/lib
java -Djava.library.path=$LD_LIBRARY_PATH -jar watchdog.jar

