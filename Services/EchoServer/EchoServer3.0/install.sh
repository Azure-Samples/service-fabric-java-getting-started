azuresfcli servicefabric application package copy --application-package-path EchoServerApplication3 --image-store-connection-string fabric:ImageStore
azuresfcli servicefabric application type register --application-type-build-path EchoServerApplication3

#below command upgrade EchoServer App to 3.0
azuresfcli servicefabric application upgrade start --application-name fabric:/EchoServerApplication --target-application-type-version 3.0.0 --upgrade-kind 1 --rolling-upgrade-mode Monitored
