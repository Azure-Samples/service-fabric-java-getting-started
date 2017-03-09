#!/bin/bash
set -x
azure servicefabric application package copy VisualObjectApplication fabric:ImageStore > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Application copy failed."
    exit 1
fi
azure servicefabric application type register VisualObjectApplication > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Application type registration failed."
    exit 1
fi
version=$(sed -e "s/xmlns/ignore/" VisualObjectApplication/ApplicationManifest.xml | xmllint --xpath "string(//ApplicationManifest/@ApplicationTypeVersion)" -)
eval azure servicefabric application upgrade start --application-name fabric:/VisualObjectApplication --target-application-type-version ${version} --rolling-upgrade-mode Monitored > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Upgrade of application failed."
    exit 1
fi