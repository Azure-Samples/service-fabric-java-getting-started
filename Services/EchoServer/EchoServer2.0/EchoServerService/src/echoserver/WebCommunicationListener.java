// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package echoserver;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import microsoft.servicefabric.services.communication.runtime.CommunicationListener;
import microsoft.servicefabric.services.runtime.StatelessServiceContext;
import system.fabric.description.EndpointResourceDescription;

class WebCommunicationListener implements CommunicationListener {

    private static final Logger logger = Logger.getLogger(WebCommunicationListener.class.getName());
    private static final String webEndpointName = "WebEndpoint";
    
    private StatelessServiceContext context;
    private HttpServer server;
    private int port;
    
    public WebCommunicationListener(StatelessServiceContext context) {
        this.context = context;
        this.port = getPort();
    }

    @Override
    public CompletableFuture<String> openAsync() {
        CompletableFuture<String> str = new CompletableFuture<>();
        String address = String.format("http://%s:%d/getMessage", this.context.nodeContext().getIpAddressOrFQDN(), this.port);
        str.complete(address);
        try {
            server = new HttpServer(port);
            server.startServer();
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Error starting http server on port %d, %s", this.port, e.toString()));
            throw new RuntimeException(e);
        }

        return str;
    }

    @Override
    public CompletableFuture<?> closeAsync() {
        CompletableFuture<Boolean> task = new CompletableFuture<>();
        task.complete(Boolean.TRUE);
        if (server != null) {
            server.stopServer();
        }

        return task;
    }

    @Override
    public void abort() {
        if (server != null) {
            server.stopServer();
        }
    }
    
    private int getPort()
    {
        EndpointResourceDescription endpoint = this.context.codePackageActivationContext().getEndpoint(webEndpointName);
        return endpoint.getPort();
    }
}
