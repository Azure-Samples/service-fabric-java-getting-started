// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package gateway;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import microsoft.servicefabric.services.communication.runtime.CommunicationListener;
import microsoft.servicefabric.services.communication.runtime.ServiceInstanceListener;
import microsoft.servicefabric.services.runtime.StatelessService;
import microsoft.servicefabric.services.runtime.StatelessServiceContext;
import system.fabric.ConfigurationPackage;
import system.fabric.ConfigurationProperty;
import system.fabric.ConfigurationSection;
import system.fabric.ConfigurationSettings;
import system.fabric.description.EndpointResourceDescription;

public class GatewayService extends StatelessService {

    private static final Logger logger = Logger.getLogger(GatewayService.class.getName());
    private static final String serviceKey = "Service";
    private static final String actorKey = "Actor";
    private static final String webEndpointName = "WebEndpoint";

    @Override
    protected List<ServiceInstanceListener> createServiceInstanceListeners() 
    {
        List<ServiceInstanceListener> listeners = new ArrayList<ServiceInstanceListener>();
        listeners.add(new ServiceInstanceListener((context) -> createCommunicationListener(context)));
        return listeners;
    }; 
    
    private CommunicationListener createCommunicationListener(StatelessServiceContext context)
    {
        ConfigurationPackage configPackage = this.context().codePackageActivationContext().getConfigurationPackageObject("Config");
        ConfigurationSettings settings = configPackage.getSettings();
        HashMap<String, ConfigurationSection> sections = null;
        if (settings != null) {
            sections = settings.getSections();
        }

        ConfigurationSection serviceSection = null;
        if (sections != null) {
            if (sections.containsKey(GatewayService.serviceKey)) {
                serviceSection = sections.get(GatewayService.serviceKey);
            }
        }

        HashMap<String, ConfigurationProperty> properties = null;
        if (serviceSection != null) {
            properties = serviceSection.getParameters();
        }

        EndpointResourceDescription endpoint = this.context().codePackageActivationContext().getEndpoint(webEndpointName);
        int port = endpoint.getPort();
        
        URI serviceName = null;
        if (properties != null) 
        {
            if (properties.containsKey(GatewayService.actorKey)) {
                    ConfigurationProperty nameProperty = properties.get(GatewayService.actorKey);
                    try {
                        serviceName = new URI(nameProperty.getValue());
                    } catch (URISyntaxException e) {
                        logger.log(Level.SEVERE, "Unable to parse service name URI {0}", nameProperty.getValue());
                        throw new RuntimeException(e);
                    }
                }
        }

        if (serviceName == null)
        {
            String errorMsg = "Unable to fetch service name from settings";
            logger.log(Level.SEVERE, errorMsg);
            throw new RuntimeException(errorMsg);
        }
        
        return new HttpCommunicationListener(serviceName, port, this.context());
    }
}
