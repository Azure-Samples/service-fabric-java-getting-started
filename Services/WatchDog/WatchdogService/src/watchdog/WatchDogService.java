// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package watchdog;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import microsoft.servicefabric.services.communication.client.ServicePartitionClientImpl;
import microsoft.servicefabric.services.communication.runtime.ServiceInstanceListener;
import microsoft.servicefabric.services.communication.client.ExceptionHandler;
import microsoft.servicefabric.services.runtime.StatelessService;
import system.fabric.ConfigurationPackage;
import system.fabric.ConfigurationProperty;
import system.fabric.ConfigurationSection;
import system.fabric.ConfigurationSettings;
import system.fabric.description.EndpointResourceDescription;

public class WatchDogService extends StatelessService {

    private static final Logger logger = Logger.getLogger(WatchDogService.class.getName());
    private static final String serviceKey = "Service";
    private static final String monitorKey = "Monitor";
    private static final String nameKey = "Name";
    private static final String typeKey = "ServiceType";
    private static final String healthCheckAfterKey = "HealthCheckAfter";
    private static final String webEndpointName = "WebEndpoint";
    
    private URI serviceName = null;
    
    @Override
    protected List<ServiceInstanceListener> createServiceInstanceListeners()
    {
        if (null == serviceName)
        {
            try
            {
                String nameProperty = getPropertyFromConfig(WatchDogService.nameKey);
                if (nameProperty != null && !nameProperty.isEmpty())
                {
                    serviceName = new URI(nameProperty);
                }
                else
                {
                    serviceName = new URI("fabric:/EchoServerApplication/EchoServer");
                }
            }
            catch (URISyntaxException ex)
            {
                throw new RuntimeException(ex);
            }
        }
        
        EndpointResourceDescription endpoint = this.context().codePackageActivationContext().getEndpoint(webEndpointName);
        int port = endpoint.getPort();
        
        List<ServiceInstanceListener> listeners = new ArrayList<ServiceInstanceListener>();
        listeners.add(new ServiceInstanceListener((context) -> new HttpCommunicationListener(serviceName, context, port)));
        return listeners;
    }; 
    
    @Override
    protected CompletableFuture<?> runAsync() {
        String monitorValue = getPropertyFromConfig(WatchDogService.monitorKey);
        final Boolean monitorOn = Boolean.valueOf(monitorValue);
        
        if (null == serviceName)
        {
            try
            {
                String nameProperty = getPropertyFromConfig(WatchDogService.nameKey);
                if (nameProperty != null && !nameProperty.isEmpty())
                {
                    serviceName = new URI(nameProperty);
                }
                else
                {
                    serviceName = new URI("fabric:/EchoServerApplication/EchoServer");
                }
            }
            catch (URISyntaxException ex)
            {
                throw new RuntimeException(ex);
            }
        }
        
        final String serviceType = getPropertyFromConfig(WatchDogService.typeKey);
        
        String healthCheckAfterValue = getPropertyFromConfig(WatchDogService.healthCheckAfterKey);
        int healthCheckAfter = Integer.valueOf(healthCheckAfterValue);
        
        return CompletableFuture.runAsync(() -> {
            try {
                if (monitorOn && serviceType.equals("EchoServerType")) {
                    List<ExceptionHandler> exceptionHandlers = new ArrayList<ExceptionHandler>(){{
                        add(new CommunicationExceptionHandler());
                        }};
                        
                    ServicePartitionClientImpl<HttpCommunicationClient> client
                            = new ServicePartitionClientImpl<>(new HttpCommunicationClientFactory(null, exceptionHandlers), serviceName);
                    Monitor mon = new Monitor();
                    while (true) {
                        try {
                            client.invokeWithRetryAsync((c) -> {
                                CompletableFuture<Boolean> b = new CompletableFuture<>();
                                mon.reportHealth(c);
                                b.complete(Boolean.TRUE);
                                return b;
                            }).get();
                            Thread.sleep(healthCheckAfter);
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private String getPropertyFromConfig(String key)
    {
        String propertyValue = null;
        ConfigurationPackage configPackage = this.context().codePackageActivationContext().getConfigurationPackageObject("Config");
        ConfigurationSettings settings = configPackage.getSettings();
        HashMap<String, ConfigurationSection> sections = null;
        if (settings != null) {
            sections = settings.getSections();
        }

        ConfigurationSection serviceSection = null;
        if (sections != null) {
            if (sections.containsKey(WatchDogService.serviceKey)) {
                serviceSection = sections.get(WatchDogService.serviceKey);
            }
        }

        HashMap<String, ConfigurationProperty> properties = null;
        if (serviceSection != null) {
            properties = serviceSection.getParameters();
        }
        
        if (properties != null)
        {
            if (properties.containsKey(key)) {
                propertyValue = properties.get(key).getValue();
            }
        }
        
        return propertyValue;
    }
}
