#!/bin/bash
set -x
create_app()
{
  if [ $# -eq 0 ]; then
    sfctl application create --app-name fabric:/WatchdogApplication --app-type WatchdogApplicationType --app-version 1.0.0
  else
    sfctl application create --app-name fabric:/WatchdogApplication --app-type WatchdogApplicationType --app-version 1.0.0 --parameters "{\"InstanceCount\":\"-1\"}"
  fi
}
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkg="$DIR/../WatchdogApplication"

sfctl application upload --path $appPkg --show-progress
sfctl application provision --application-type-build-path WatchdogApplication

if [ $# -eq 0 ]
  then
    echo "No arguments supplied, proceed with default instanceCount of 1"
    create_app
  elif [ $1 = "onebox" ]
  then
    echo "Onebox environment, proceed with default instanceCount of 1."
    create_app
  elif [ $1 = "multinode" ]
  then
    echo "Multinode env, proceed with default instanceCount of -1"
    create_app 1
fi
