#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkg="$DIR/../CounterActorApplication"

sfctl application upload --path $appPkg --show-progress
if [ $? -ne 0 ]; then
    echo "Application copy failed."
    exit 1
fi

sfctl application provision --application-type-build-path CounterActorApplication
if [ $? -ne 0 ]; then
    echo "Application type registration failed."
    exit 1
fi

version=$(sed -e "s/xmlns/ignore/" $appPkg/ApplicationManifest.xml | xmllint --xpath "string(//ApplicationManifest/@ApplicationTypeVersion)" -)
eval sfctl application upgrade --app-id CounterActorApplication --app-version ${version} --parameters ""
if [ $? -ne 0 ]; then
    echo "Upgrade of application failed."
    exit 1
fi