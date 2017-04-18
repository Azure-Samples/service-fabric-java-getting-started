#!/bin/bash
set -x

callerPath=${0}
if [[ "$callerPath" =~ "Scripts" ]];then
 appPkg="CounterActorApplication"
else
 appPkg="../CounterActorApplication"
fi

azure servicefabric application package copy $appPkg fabric:ImageStore
azure servicefabric application type register CounterActorApplication
azure servicefabric application create fabric:/CounterActorApplication  CounterActorApplicationType  1.0.0
