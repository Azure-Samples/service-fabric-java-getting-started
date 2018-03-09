package statelessservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.ArrayList;

import system.fabric.CancellationToken;
import system.fabric.description.EndpointResourceDescription;

import microsoft.servicefabric.services.communication.runtime.ServiceInstanceListener;
import microsoft.servicefabric.services.runtime.StatelessService;

public class VotingWebService extends StatelessService {

    private static final String webEndpointName = "WebEndpoint";

	private URI serviceName = null; 
    @Override
    protected List<ServiceInstanceListener> createServiceInstanceListeners() {
        // TODO: If your service needs to handle user requests, return the list of ServiceInstanceListeners from here.
        try {
			serviceName = new URI("fabric:/VotingApplication/VotingDataService");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
        
        EndpointResourceDescription endpoint = this.getServiceContext().getCodePackageActivationContext().getEndpoint(webEndpointName);
        int port = endpoint.getPort();
        
        List<ServiceInstanceListener> listeners = new ArrayList<ServiceInstanceListener>();
        listeners.add(new ServiceInstanceListener((context) -> new HttpCommunicationListener(serviceName, context, port)));
        return listeners;
    }

    @Override
    protected CompletableFuture<?> runAsync(CancellationToken cancellationToken) {
        // TODO: Replace the following with your own logic.
        return super.runAsync(cancellationToken);
    }
}
