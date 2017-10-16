// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectwebservice;

import java.util.HashMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import microsoft.servicefabric.services.communication.runtime.ServiceInstanceListener;
import microsoft.servicefabric.services.runtime.StatelessService;
import system.fabric.ConfigurationPackage;
import system.fabric.ConfigurationProperty;
import system.fabric.ConfigurationSection;
import system.fabric.ConfigurationSettings;
import system.fabric.description.EndpointResourceDescription;

public class VisualObjectWebService extends StatelessService {

    private static final Logger logger = Logger.getLogger(VisualObjectWebService.class.getName());

    @Override
    protected List<ServiceInstanceListener> createServiceInstanceListeners() {
        ConfigurationPackage configPackage = this.getServiceContext().getCodePackageActivationContext().getConfigurationPackageObject("Config");
        ConfigurationSettings settings = configPackage.getSettings();
        HashMap<String, ConfigurationSection> sections = null;
        if (settings != null) {
            sections = settings.getSections();
        }

        ConfigurationSection serviceSection = null;
        if (sections != null) {
            serviceSection = sections.get("VisualObjectsBoxSettings");
        }

        HashMap<String, ConfigurationProperty> properties = null;
        if (serviceSection != null) {
            properties = serviceSection.getParameters();
        }

        String serviceName = null;
        int numObjects = 0;
        
        EndpointResourceDescription endpoint = this.getServiceContext().getCodePackageActivationContext().getEndpoint("WebEndpoint");
        int port = endpoint.getPort();
        
        String appName = this.getServiceContext().getCodePackageActivationContext().getApplicationName();
        if (serviceSection != null) {
            serviceName = properties.get("ServiceName").getValue(); 
            String count = properties.get("ObjectCount").getValue(); 
            numObjects = Integer.parseInt(count);
        }

        logger.log(Level.INFO, "AppName:{0} ServiceName:{1} NumOfObject:{2}", new Object[]{appName, serviceName, numObjects});
        
        ArrayList<ServiceInstanceListener> listeners = new ArrayList<>();
        final String srvName = serviceName;
        final int num = numObjects;
        
        listeners.add(new ServiceInstanceListener((context) -> {
            try {
                return new WebCommunicationListener(new VisualObjectsBoxImpl(new URI(appName + "/" + srvName), num), context, port);
            	//return new WebCommunicationListener(new VisualObjectsBoxImpl(new URI(appName + "/" + srvName), 1), context, port);
            } catch (URISyntaxException ex) {
                logger.log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }));
        
        return listeners;
    }
}
