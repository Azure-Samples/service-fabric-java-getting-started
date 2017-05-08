#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkg="$DIR/../EchoServerApplication2"

azure servicefabric application package copy $appPkg fabric:ImageStore > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Application copy failed."
    exit 1
fi
azure servicefabric application type register EchoServerApplication2 > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Application type registration failed."
    exit 1
fi
version=$(sed -e "s/xmlns/ignore/" $appPkg/ApplicationManifest.xml | xmllint --xpath "string(//ApplicationManifest/@ApplicationTypeVersion)" -)
eval azure servicefabric application upgrade start --application-name fabric:/EchoServerApplication --target-application-type-version ${version} --rolling-upgrade-mode Monitored > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Upgrade of application failed."
    exit 1
fi