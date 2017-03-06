#!/bin/bash
azure servicefabric application package copy JenkinsSF fabric:ImageStore
azure servicefabric application type register JenkinsSF
azure servicefabric application create fabric:/jenkinsSF JenkinsSFAppType 1.0
azure servicefabric service create --application-name fabric:/jenkinsSF --service-name fabric:/jenkinsSF/service1 --service-type-name JenkinsOnSFServiceType --instance-count 1 --service-kind Stateless --partition-scheme Singleton --placement-constraints "NodeName == _Node_0" 
