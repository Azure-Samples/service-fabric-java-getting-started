#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkg="$DIR/../GatewayApplication"

sfctl application upload --path $appPkg --show-progress
sfctl application provision --application-type-build-path GatewayApplication

if [ $# -eq 0 ]
  then
    echo "No arguments supplied, proceed with default instanceCount of 1"
    sfctl application create --app-name fabric:/GatewayApplication --app-type GatewayApplicationType --app-version 1.0.0
  elif [ $1 = 0 ]
  then
    echo "Onebox environment, proceed with default instanceCount of 1."
    sfctl application create --app-name fabric:/GatewayApplication --app-type GatewayApplicationType --app-version 1.0.0
  elif [ $1 = 1 ]
  then
    echo "Multinode env, proceed with default instanceCount of -1"
    sfctl application create --app-name fabric:/GatewayApplication --app-type GatewayApplicationType --app-version 1.0.0 --parameters "{\"InstanceCount\":\"-1\"}"
fi
