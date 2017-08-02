#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkg="$DIR/../EchoServerApplication2"

sfctl application upload --path $appPkg --show-progress
sfctl application provision --application-type-build-path EchoServerApplication2
eval sfctl application upgrade start --app-id EchoServerApplication --app-version 2.0.0 --parameters "" --mode "Monitored"
