package statefulservice;

import java.util.concurrent.CompletableFuture;

import microsoft.servicefabric.data.ReliableStateManager;
import microsoft.servicefabric.services.communication.runtime.CommunicationListener;
import system.fabric.CancellationToken;
import system.fabric.ServiceContext;
import system.fabric.StatefulServiceContext;
import system.fabric.description.EndpointResourceDescription;

public class HttpCommunicationListener implements CommunicationListener {

    private ServiceContext serviceContext;
    private String listeningAddress;
    private HttpServer httpServer;
    private ReliableStateManager stateManager;
    public HttpCommunicationListener(ServiceContext serviceContext, ReliableStateManager stateManager) {
        this.serviceContext = serviceContext;
        this.stateManager = stateManager;
    }
    @Override
    public CompletableFuture<String> openAsync(CancellationToken cancellationToken) {
        EndpointResourceDescription serviceEndpoint = this.serviceContext.getCodePackageActivationContext().getEndpoint("ServiceEndpoint");
        int port = serviceEndpoint.getPort();
        if (serviceContext instanceof StatefulServiceContext){
            String baseAddress = "/";
            
            this.httpServer = new HttpServer(baseAddress, port, stateManager);
            this.httpServer.start();
                    
        } else {
            // TODO
        }
        this.listeningAddress = String.format("http://%s:%d/", this.serviceContext.getNodeContext().getIpAddressOrFQDN(), port);
        return CompletableFuture.completedFuture(this.listeningAddress);
    }

    @Override
    public CompletableFuture<?> closeAsync(CancellationToken cancellationToken) {
        this.httpServer.stop();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void abort() {
        this.httpServer.stop();
    }

}
