#!/bin/bash

sfctl application delete --application-id WatchdogApplication
sfctl application unprovision --application-type-name WatchdogApplicationType --application-type-version 1.0.0
sfctl store delete --content-path WatchdogApplication
