#!/bin/bash -e
azure servicefabric application package copy JenkinsSF fabric:ImageStore
azure servicefabric application type register JenkinsSF
azure servicefabric application create fabric:/JenkinsSF JenkinsSFApplicationType 1.0
azure servicefabric service create --application-name fabric:/JenkinsSF --service-name fabric:/JenkinsSF/ContainerService --service-type-name JenkinsOnSFServiceType --instance-count 1 --service-kind Stateless --partition-scheme Singleton
