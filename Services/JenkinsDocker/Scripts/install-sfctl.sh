#!/bin/bash -e

sfctl application upload --path JenkinsSF --show-progress
sfctl application provision --application-type-build-path JenkinsSF
sfctl application create --app-name fabric:/JenkinsSF --app-type JenkinsSFApplicationType --app-version 1.0
sfctl service create --app-id JenkinsSF --name fabric:/JenkinsSF/ContainerService --service-type JenkinsOnSFServiceType --stateless --instance-count 1 --singleton-scheme
