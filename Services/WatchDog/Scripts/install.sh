azure servicefabric application package copy WatchdogApplication fabric:ImageStore
azure servicefabric application type register WatchdogApplication

if [ $# -eq 0 ]
  then
    echo "No arguments supplied, proceed with default instanceCount of 1"
    azure servicefabric application create --application-name fabric:/WatchdogApplication  --application-type-name WatchdogApplicationType --application-type-version 1.0.0
  elif [ $1 = 0 ]
  then
    echo "Onebox environment, proceed with default instanceCount of 1."
    azure servicefabric application create --application-name fabric:/WatchdogApplication  --application-type-name WatchdogApplicationType --application-type-version 1.0.0
  elif [ $1 = 1 ]
  then
    echo "Multinode env, proceed with default instanceCount of -1"
    azure servicefabric application create --application-name fabric:/WatchdogApplication  --application-type-name WatchdogApplicationType --application-type-version 1.0.0 --application-parameter "[{\"key\":\"InstanceCount\",\"value\":\"-1\"}]"
fi
