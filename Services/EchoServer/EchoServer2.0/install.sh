azure servicefabric application package copy --application-package-path EchoServerApplication2 --image-store-connection-string fabric:ImageStore
azure servicefabric application type register --application-type-build-path EchoServerApplication2

# Used below command to upgrade from version 1.0 to version 2.0
azure servicefabric application upgrade start --application-name fabric:/EchoServerApplication --target-application-type-version 2.0.0 --upgrade-kind 1 --rolling-upgrade-mode Monitored
