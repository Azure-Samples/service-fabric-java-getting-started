#!/bin/bash
BASEDIR=$(dirname $0)
cd $BASEDIR
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$(pwd)/lib
java -Djava.library.path=$LD_LIBRARY_PATH -jar visualobjectactor.service.jar

