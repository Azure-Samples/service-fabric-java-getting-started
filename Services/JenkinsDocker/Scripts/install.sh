#!/bin/bash
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
appPkg="$DIR/../JenkinsSF"

nodeName=`azure servicefabric node show | tail -n +3 | head -n -1 | cut -f2-3 -d':' |grep "name" | head -1 | cut -f2 -d':' | tr -d " '," | sed "s,\x1B\[[0-9;]*[a-zA-Z],,g"` 
echo Deploying Jenkins container service on :$nodeName
azure servicefabric application package copy $appPkg fabric:ImageStore
azure servicefabric application type register JenkinsSF
azure servicefabric application create fabric:/JenkinsSF JenkinsSFApplicationType 1.0
azure servicefabric service create --application-name fabric:/JenkinsSF --service-name fabric:/JenkinsSF/ContainerService --service-type-name JenkinsOnSFServiceType --instance-count 1 --service-kind Stateless --partition-scheme Singleton --placement-constraints "NodeName==$nodeName"