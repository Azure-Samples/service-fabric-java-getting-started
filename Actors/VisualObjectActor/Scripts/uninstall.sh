#!/bin/bash

sfctl application delete --application-id VisualObjectApplication
sfctl application unprovision --application-type-name VisualObjectsApplicationType --application-type-version 1.0.0
sfctl store delete --content-path VisualObjectApplication
