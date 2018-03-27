---
services: service-fabric
platforms: java
author: vturecek, raunakp, suhuruli
---

# Getting started with Service Fabric with Java

This repository contains a set of simple sample projects to help you getting started with Service Fabric on Linux using java as the development language. 

## Prerequisites 

1. To develop Service Fabric Reliable Services & Actor you need to use a [Mac](https://docs.microsoft.com/en-us/azure/service-fabric/service-fabric-get-started-mac) or [Linux](https://docs.microsoft.com/en-us/azure/service-fabric/service-fabric-get-started-linux) developer machine. 
2. If you are developing Java applications that do not use the Reliable Services API provided by Service Fabric, you can develop in any environment. 

## How the samples are organized

### reliable-services-actor-sample

This folder contains an application that uses the Service Fabric Reliable Actors & Reliable Services. It is one application with numerous simple services written using the Service Fabric APIs. It is a good place to get started to familiarize yourself with each Reliable Actors or Reliable Services. 

### reliable-services-httpcommunication-sample

This folder contains an application composed of two microservices. The frontend service communicates with the backend service using Http. This is a good sample that illustrates how two microservices in Service Fabric can communicate with each other using Http protocols. 

### reliable-services-embedded-jetty-sample

This folder contains a service that stands up a Jetty server within a Service Fabric stateless service. The Jetty server is a very simple server that is stood up within a stateless service. 
