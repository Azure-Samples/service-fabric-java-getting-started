#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
pushd $(pwd)
cd $DIR
appPkg="$DIR/../VisualObjectApplication"

azure servicefabric application package copy --application-package-path $appPkg --image-store-connection-string fabric:ImageStore
azure servicefabric application type register --application-type-build-path VisualObjectApplication
if [ $# -eq 0 ]
  then
    echo "No arguments supplied, proceed with default instanceCount of 1"
    azure servicefabric application create --application-name fabric:/VisualObjectApplication --application-type-name VisualObjectsApplicationType --application-type-version 1.0.0
  elif [ $1 = 0 ]
  then
    echo "Onebox environment, proceed with default instanceCount of 1."
    azure servicefabric application create --application-name fabric:/VisualObjectApplication  --application-type-name VisualObjectsApplicationType --application-type-version 1.0.0
  elif [ $1 = 1 ]
  then
    echo "Multinode env, proceed with default instanceCount of -1"
    azure servicefabric application create --application-name fabric:/VisualObjectApplication  --application-type-name VisualObjectsApplicationType --application-type-version 1.0.0 --application-parameter "[{\"key\":\"InstanceCount\",\"value\":\"-1\"}]"
fi
popd