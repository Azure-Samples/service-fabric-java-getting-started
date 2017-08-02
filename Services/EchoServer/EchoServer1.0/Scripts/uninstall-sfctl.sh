#!/bin/bash

sfctl application delete --application-id EchoServerApplication
sfctl application unprovision --application-type-name EchoServerApplicationType --application-type-version 1.0.0
sfctl store delete --content-path EchoServerApplication
