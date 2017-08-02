#!/bin/bash

sfctl application delete --application-id fabric:/GatewayApplication
sfctl application unprovision --application-type-name GatewayApplicationType --application-type-version 1.0.0
sfctl store delete --content-path GatewayApplication
