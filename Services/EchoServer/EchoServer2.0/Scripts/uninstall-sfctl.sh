#!/bin/bash

sfctl application delete --application-id fabric:/EchoServerApplication
sfctl application unprovision --application-type-name EchoServerApplicationType --application-type-version 2.0.0
sfctl application unprovision --application-type-name EchoServerApplicationType --application-type-version 1.0.0
sfctl store delete --content-path EchoServerApplication
