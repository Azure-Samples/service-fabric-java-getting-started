#!/bin/bash -ex

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkgPath="$DIR/../VotingApplication"

if [[ "$#" != "0" ]];then 
	version="$1"
else 
	version="1.0.0"
fi

sfctl application upload --path $appPkgPath
if [ $? -ne 0 ]; then
    echo "Application copy failed."
    exit 1
fi

sfctl application provision --application-type-build-path VotingApplication 
if [ $? -ne 0 ]; then
    echo "Application type registration failed."
    exit 1
fi

sfctl application create --app-name fabric:/VotingApplication --app-type VotingApplicationType  --app-version $version
if [ $? -ne 0 ]; then
    echo "Application creation failed."
    exit 1
fi

echo "Install script executed successfully."
