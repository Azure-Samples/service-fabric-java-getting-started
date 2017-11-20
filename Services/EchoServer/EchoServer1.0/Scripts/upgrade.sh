#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkg="$DIR/../EchoServerApplication"

if [[ "$#" != "0" ]];then
        version="$1"
else
        version="1.0.0"
fi

sfctl application upload --path $appPkg --show-progress
if [ $? -ne 0 ]; then
    echo "Application copy failed."
    exit 1
fi

sfctl application provision --application-type-build-path EchoServerApplication
if [ $? -ne 0 ]; then
    echo "Application type registration failed."
    exit 1
fi

# Check parameters to see if InstanceCount was previously specified
params=$(sfctl application info --application-id EchoServerApplication --query "parameters[].[key, value][]")
if [[ $params =~ "InstanceCount" ]]; then
    sfctl application upgrade --app-id fabric:/EchoServerApplication --app-version ${version} --parameters "{\"InstanceCount\":\"-1\"}" --mode Monitored
else
    sfctl application upgrade --app-id fabric:/EchoServerApplication --app-version ${version} --parameters [] --mode "Monitored"
fi

if [ $? -ne 0 ]; then
    echo "Upgrade of application failed."
    exit 1
fi
