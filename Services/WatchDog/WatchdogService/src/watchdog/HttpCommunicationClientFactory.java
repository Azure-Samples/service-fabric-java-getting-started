// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package watchdog;

import java.util.concurrent.CompletableFuture;

import java.util.List;
import microsoft.servicefabric.services.client.ServicePartitionResolver;
import microsoft.servicefabric.services.communication.client.CommunicationClientFactoryBase;
import microsoft.servicefabric.services.communication.client.ExceptionHandler;

public class HttpCommunicationClientFactory extends CommunicationClientFactoryBase<HttpCommunicationClient> {

    protected HttpCommunicationClientFactory(ServicePartitionResolver servicePartitionResolver, List<ExceptionHandler> exceptionHandlers)
    {
        super(servicePartitionResolver, exceptionHandlers, null);
    }

    @Override
    protected boolean validateClient(HttpCommunicationClient client) {
        // client with persistent connections should be validated here.
        // HTTP clients don't hold persistent connections, so no validation needs to be done.
        return true;
    }

    @Override
    protected boolean validateClient(String endpoint, HttpCommunicationClient client) {
        // client with persistent connections should be validated here.
        // HTTP clients don't hold persistent connections, so no validation needs to be done.
        return true;
    }

    @Override
    protected CompletableFuture<HttpCommunicationClient> createClientAsync(String endpoint) {
        // clients that maintain persistent connections to a service should 
        // create that connection here.
        // an HTTP client doesn't maintain a persistent connection.
        
        HttpCommunicationClient client = new HttpCommunicationClient(endpoint);
        CompletableFuture<HttpCommunicationClient> ctask = new CompletableFuture<>();
        ctask.complete(client);
        return ctask;
    }

    @Override
    protected void abortClient(HttpCommunicationClient client) {
        // client with persistent connections should be abort their connections here.
        // HTTP clients don't hold persistent connections, so no action is taken.
    }
}
