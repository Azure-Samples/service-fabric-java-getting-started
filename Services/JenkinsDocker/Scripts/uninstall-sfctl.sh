#!/bin/bash

sfctl application delete --application-id JenkinsSF
sfctl application unprovision --application-type-name JenkinsSFApplicationType --application-type-version 1.0
sfctl store delete --content-path JenkinsSF
