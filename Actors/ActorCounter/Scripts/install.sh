#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
pushd $(pwd)
cd $DIR
appPkg="$DIR/../CounterActorApplication"

azure servicefabric application package copy $appPkg fabric:ImageStore
azure servicefabric application type register CounterActorApplication
azure servicefabric application create fabric:/CounterActorApplication  CounterActorApplicationType  1.0.0

popd
