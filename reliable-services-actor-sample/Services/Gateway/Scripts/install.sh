#!/bin/bash

create_app()
{
  sfctl application create --app-name fabric:/GatewayApplication --app-type GatewayApplicationType --app-version 1.0.0 --parameters $1
}
print_help()
{
  echo "Additional Options"
  echo "-onebox (Default): If you are deploying application on one box cluster"
  echo "-multinode: If you are deploying application on a multi node cluster"
}

if [ "$1" = "--help" ]
  then
    print_help
    exit 0
fi
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkg="$DIR/../GatewayApplication"

sfctl application upload --path $appPkg --show-progress
sfctl application provision --application-type-build-path GatewayApplication

if [ $# -eq 0 ]
  then
    echo "No arguments supplied, proceed with default instanceCount of 1"
    create_app {}
  elif [ $1 = "-onebox" ]
  then
    echo "Onebox environment, proceed with default instanceCount of 1."
    create_app {}
  elif [ $1 = "-multinode" ]
  then
    echo "Multinode env, proceed with default instanceCount of -1"
    create_app "{\"InstanceCount\":\"-1\"}"
fi
