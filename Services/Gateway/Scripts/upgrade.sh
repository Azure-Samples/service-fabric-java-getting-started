#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkg="$DIR/../GatewayApplication"

sfctl application upload --path $appPkg --show-progress
if [ $? -ne 0 ]; then
    echo "Application copy failed."
    exit 1
fi

sfctl application provision --application-type-build-path GatewayApplication
if [ $? -ne 0 ]; then
    echo "Application type registration failed."
    exit 1
fi

# Check parameters to see if InstanceCount was previously specified
params=$(sfctl application info --application-id GatewayApplication --query "parameters[].[key, value][]")
version=$(sed -e "s/xmlns/ignore/" $appPkg/ApplicationManifest.xml | xmllint --xpath "string(//ApplicationManifest/@ApplicationTypeVersion)" -)
if [[ $params =~ "InstanceCount" ]]; then
    sfctl application upgrade --app-id GatewayApplication --app-version ${version} --parameters "{\"InstanceCount\":\"-1\"}"
else
    sfctl application upgrade --app-id GatewayApplication --app-version ${version} --parameters ""
fi
if [ $? -ne 0 ]; then
    echo "Upgrade of application failed."
    exit 1
fi
