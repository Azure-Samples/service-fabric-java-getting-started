package statelessservice;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import system.fabric.CancellationToken;
import system.fabric.description.EndpointResourceDescription;
import microsoft.servicefabric.services.communication.runtime.ServiceInstanceListener;
import microsoft.servicefabric.services.runtime.StatelessService;

public class EmbeddedJettyServerService extends StatelessService {

	private static final Logger logger = Logger.getLogger(EmbeddedJettyServerService.class.getName());

    @Override
    protected List<ServiceInstanceListener> createServiceInstanceListeners() {
    	
    		Map<String, EndpointResourceDescription> endpointsMap = this.getServiceContext().getCodePackageActivationContext().getEndpoints();
        
    		logger.log(Level.INFO, Integer.toString(endpointsMap.size()));
        List<ServiceInstanceListener> listeners = new ArrayList<ServiceInstanceListener>();
        
        for (String key: endpointsMap.keySet()) {
        		int port = endpointsMap.get(key).getPort();
    			logger.log(Level.INFO, "Key is: "+ key + "Port is: " + port);
            listeners.add(new ServiceInstanceListener((context) -> new HttpCommunicationListener(context, port)));        		
        }
        return listeners;
    }
    
    @Override
    protected CompletableFuture<?> runAsync(CancellationToken cancellationToken) {
        return super.runAsync(cancellationToken);
    }
}