azure servicefabric application package copy --application-package-path EchoServerApplication --image-store-connection-string fabric:ImageStore
azure servicefabric application type register --application-type-build-path EchoServerApplication
if [ $# -eq 0 ]
  then
    echo "No arguments supplied, proceed with default instanceCount of 1"
    azure servicefabric application create --application-name fabric:/EchoServerApplication  --application-type-name EchoServerApplicationType --application-type-version 1.0.0
  elif [ $1 = 0 ]
  then
    echo "Onebox environment, proceed with default instanceCount of 1."
    azure servicefabric application create --application-name fabric:/EchoServerApplication  --application-type-name EchoServerApplicationType --application-type-version 1.0.0
  elif [ $1 = 1 ]
  then
    echo "Multinode env, proceed with default instanceCount of -1"
    azure servicefabric application create --application-name fabric:/EchoServerApplication  --application-type-name EchoServerApplicationType --application-type-version 1.0.0 --application-parameter "[{\"key\":\"InstanceCount\",\"value\":\"-1\"}]"
fi
