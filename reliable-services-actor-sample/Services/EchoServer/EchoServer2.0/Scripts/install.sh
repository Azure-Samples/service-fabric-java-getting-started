#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkg="$DIR/../EchoServerApplication2"

sfctl application upload --path $appPkg --show-progress -t 1200
sfctl application provision --application-type-build-path EchoServerApplication2

# Check parameters to see if InstanceCount was previously specified
params=$(sfctl application info --application-id EchoServerApplication --query "parameters[].[key, value][]")
if [[ $params =~ "InstanceCount" ]]; then
    sfctl application upgrade --app-id EchoServerApplication --app-version 2.0.0 --parameters "{\"InstanceCount\":\"-1\"}" --mode "Monitored"
else
    sfctl application upgrade --app-id EchoServerApplication --app-version 2.0.0 --parameters "" --mode "Monitored"
fi
