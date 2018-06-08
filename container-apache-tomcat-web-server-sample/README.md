# Apache Tomcat Web Server Sample

This sample contains an Apache Tomcat Web server that hosts a simple Java application. This entire web server is then packaged up and deployed as a container. The instructions on how to use this sample are in the following [article]().

## Contents 

The entire application is self contained and doesn't need to be built. The ApacheTomcat folder contains the necessary packages for this sample. The important folders in the ApacheTomcat folder is defined in the table below. 

Folder | Description |
--- | --- |
bin | Helper Tomcat scripts to manage the standalone app server etc. |
conf | Tomcat configuration files |
lib | Necessary jar files to build and run the application server and the applicationContains JSON files that define the cluster the application is targetted for.|
webapps/hello | This folder contains the Hello World applications | 