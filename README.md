---
services: service-fabric
platforms: java
author: vturecek, raunakp, saysa
---

# Getting started with Service Fabric with Java

This repository contains a set of simple sample projects to help you getting started with Service Fabric on Linux using java as the development language. As a pre requisite ensure you have the Service Fabric Java SDK installed on ubuntu box. 

## How the samples are organized

The samples are divided by the category and [Service Fabric programming model][service-fabric-programming-models] that they focus on: Reliable Actors, Reliable Services. Note that most real applications will include a mixture of the concepts and programming models.

## Actor samples
### ActorCounter

Actor counter provides an example of a very simple actor which implements a counter. Once the service is deployed you can run the testclient to see the effect of counter incrementing.  

### VisualObjects 

This sample project uses Paper.js to render a set of objects. Each object is represented by an Actor, where the location and trajectory of each is calculated on the server side by the Actor representing the object. 

This sample creates multiple Visual actor which keeps on moving in the plane. 
To run the sample, execute gradle and install application by executing install.sh, or install-sfctl.sh (if using the Azure Service Fabric command line). Once the application has started, go to http://localhost:8507/index.html. Try opening it in multiple browser windows or on multiple machines to see how the server side calculation produces the same result on every screen.

It also provides a clear demonstration of how Service Fabric performs rolling upgrades as the behavior of the shapes can be seen to gradually change as the upgrade proceeds across the upgrade domains in the cluster. To see how to perform a rolling upgrade using the Visual Objects sample, see the [application upgrade tutorial][app-upgrade-tutorial].

## Service Samples
### EchoServer

This stateless service sample demostrates a simple web server. Go to EchoServer1.0 and after deploying application, go to browser and type in http://localhost:8508/getMessage, you should see '[version 1.0]Hello World !!!'. If you type in http://localhost:8508/getMessage/?Raunak, you should see '[version 1.0]Hello Raunak!!!'

EchoServer2.0 sample demostrates upgrade of stateless service. Both the applications are almost same except for the version number returned back upon web query. Run the install.sh (or install-sfctl.sh) to upgrade from version 1.0 to version 2.0. After upgrading the application, go to the browser and type in http://localhost:8508/getMessage, to see '[version 2.0]Hello World !!!'. If you type in http://localhost:8508/getMessage/?Raunak, you should see '[version 2.0]Hello Raunak!!!'

### Gateway

This stateless service sample demostrates writing a web frontend for your actor service. This is currently acting as gateway for the Actor Counter application. Once you have deployed the Actor Counter and Gateway application go to url http://localhost:12346/Actor/Actor1 to see the counter value being fetched from the actor service. Refresh the web page to see how the counter value getting incremented. 

### Watchdog

This stateless service demostrates a watch dog application which is monitoring the EchoServer application. Once you have depployed EchoServer and WatchDog application, you would see that if EchoServer application goes down or for some reason the applications web endpoint is not reachable, an error health is reported by WatchDog for the EchoServer application in the Service Fabric health subsystem. Apart from this, this application also shows inter service communication by forwarding the web requests to Echo Server application. You can view this by going to browser @ http://localhost:12345/getMessage to see the output coming from EchoServer. 

## Compiling the samples
For compiling the samples use gradle. This should be configured as part of the Service Fabric Java SDK installation.

## Deploying the samples
All the samples once compiled can be deployed immediately using the install.sh scripts located inside Scripts folder of every sample. These scripts will use the Service Fabric X-Plat CLI. If using the latest Azure Service Fabric python CLI (sfctl), then the corresponding `install-sfctl.sh` and `uninstall-sfctl.sh` scripts can be used instead. Before running the scripts you need to first connect to the cluster.

## Compiling and Deploying the samples using Service Fabric Plugin for Eclipse

### Import and Deploy GitHub Java Samples on Linux using Service Fabric Eclipse plugin
1. Clone the repository in your dev-box - ``https://github.com/Azure-Samples/service-fabric-java-getting-started.git``
2. On your Eclipse, you need to make sure, Service Fabric Plugin installed.
3. Create a new workspace.
4. Open the ``Service Fabric`` Perspective. You can change your Eclipse perspective by following the path - ``Window => Perspective => Open Perspective => Other`` and select  ``Service Fabric`` from the list you see.
5. To import the GitHub samples in Eclipse, you can go to ``File => Import => General => Existing Project into Workspace`` and select the path to say ``EchoServer1.0``, from your cloned GitHub samples.
6. Now to build and deploy you can follow the steps mentioned above in the document [Build and deploy the Service Fabric application using Eclipse](https://docs.microsoft.com/en-us/azure/service-fabric/service-fabric-get-started-eclipse#build-and-deploy-the-service-fabric-application-using-eclipse).

### Import and Deploy Github Java samples on Mac using Service Fabric Eclipse plugin
The steps mentioned above for Linux, mostly hold true here as well, with minor modifications as mentioned below.

Few things we need to keep in mind here.
  - As mentioned in the section above, you need to have your ``Vagrantfile`` to be present in a path parallel to the project you are deploying for sharing of library artifacts i.e. for example, if you are deploying ``VisualObjectActor``, then your vagrant environment should be up from the path `~/githubsamples/service-fabric-java-getting-started/Actors/`
  - In the ``build.gradle`` files of your interface(for actors) and implementations (for both Services and Actors), where ever you see the path to out java library artifacts i.e. `/opt/microsoft/sdk/servicefabric/java/packages/lib/`, replace it with a path relative to your project and vagrant-environment - `${projectDir}/../../tmp/lib/`.
  - In the root level ``build.gradle`` file, the tasks, which call the shell-scripts to connect, deploy or uninstall internally, you need the update the lines as follows -
    - In `task deploy`, the line `commandLine '/bin/bash','Scripts/deploy.sh'` needs to be changed to: `commandLine '/bin/bash','Scripts/VagrantSSHCaller.sh','Scripts/deploy.sh'`
    - Same holds true for tasks `upgrade`, `undeploy`, `clusterconnect`.


## More information

The [Service Fabric documentation][service-fabric-docs] includes a rich set of tutorials and conceptual articles, which serve as a good complement to the samples.

<!-- Links -->

[service-fabric-programming-models]: https://azure.microsoft.com/en-us/documentation/articles/service-fabric-choose-framework/
[app-upgrade-tutorial]: https://docs.microsoft.com/en-us/azure/service-fabric/service-fabric-application-upgrade-tutorial-powershell
[service-fabric-docs]: http://aka.ms/servicefabricdocs
[service-fabric-github-samples-with-eclipse]: https://docs.microsoft.com/en-us/azure/service-fabric/service-fabric-get-started-eclipse#import-and-deploy-github-java-samples-using-service-fabric-eclipse-plugin




