#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkg="$DIR/../JenkinsSF"

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

sfctl application provision --application-type-build-path JenkinsSF
if [ $? -ne 0 ]; then
    echo "Application type registration failed."
    exit 1
fi

eval sfctl application upgrade --app-id fabric:/JenkinsSF --app-version ${version} --parameters [] --mode "Monitored"
if [ $? -ne 0 ]; then
    echo "Upgrade of application failed."
    exit 1
fi
