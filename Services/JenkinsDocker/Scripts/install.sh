#!/bin/bash
nodeName=`azure servicefabric node show | tail -n +3 | head -n -1 | cut -f2-3 -d':' |grep "name" | head -1 | cut -f2 -d':' | tr -d " ',"` 
echo Installing Jenkins Container application on :$nodeName
azure servicefabric application package copy JenkinsSF fabric:ImageStore
azure servicefabric application type register JenkinsSF
azure servicefabric application create fabric:/jenkinsSF JenkinsSFAppType 1.0
azure servicefabric service create --application-name fabric:/jenkinsSF --service-name fabric:/jenkinsSF/service1 --service-type-name JenkinsOnSFServiceType --instance-count 1 --service-kind Stateless --partition-scheme Singleton --placement-constraints "NodeName == $nodeName" 
