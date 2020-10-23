#!/bin/bash -ex

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkgPath="$DIR/../EmbeddedJettyApplication"
if [[ "$#" != "0" ]];then 
	version="$1"
else 
	version="1.0.0"
fi
sfctl application upload --path $appPkgPath -t 1200
if [ $? -ne 0 ]; then
    echo "Application copy failed."
    exit 1
fi
sfctl application provision --application-type-build-path EmbeddedJettyApplication 
if [ $? -ne 0 ]; then
    echo "Application type registration failed."
    exit 1
fi
sfctl application upgrade --application-id fabric:/EmbeddedJettyApplication --application-version ${version} --parameters {} --mode "Monitored"
if [ $? -ne 0 ]; then
    echo "Upgrade of application failed."
    exit 1
fi
echo "Upgrade script executed successfully."
