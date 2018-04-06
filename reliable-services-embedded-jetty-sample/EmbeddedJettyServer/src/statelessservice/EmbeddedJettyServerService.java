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

    private static final String webEndpointName = "WebEndpoint";

    @Override
    protected List<ServiceInstanceListener> createServiceInstanceListeners() {
    	
        EndpointResourceDescription endpoint = this.getServiceContext().getCodePackageActivationContext().getEndpoint(webEndpointName);
        int port = endpoint.getPort();
        
        List<ServiceInstanceListener> listeners = new ArrayList<ServiceInstanceListener>();
        listeners.add(new ServiceInstanceListener((context) -> new HttpCommunicationListener(context, port)));
        return listeners;
    }
    
    @Override
    protected CompletableFuture<?> runAsync(CancellationToken cancellationToken) {
        return super.runAsync(cancellationToken);
    }
}