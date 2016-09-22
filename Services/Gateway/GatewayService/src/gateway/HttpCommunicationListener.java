// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import counterinterface.CounterActor;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import microsoft.servicefabric.actors.ActorExtensions;
import microsoft.servicefabric.actors.ActorId;
import microsoft.servicefabric.actors.ActorProxyBase;
import microsoft.servicefabric.services.communication.runtime.CommunicationListener;
import system.fabric.ServiceContext;

public class HttpCommunicationListener implements CommunicationListener {

    private static final Logger logger = Logger.getLogger(HttpCommunicationListener.class.getName());

    private HttpServer server;
    private final int port;
	private final URI serviceUri;
    private ServiceContext serviceContext;
    
    public HttpCommunicationListener(URI serviceUri, int port, ServiceContext serviceContext) {
        this.serviceUri = serviceUri;
        this.port = port;
        this.serviceContext = serviceContext;
    }

    private void start() {
        try {
            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(this.port), 0);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    URI r = t.getRequestURI();
                    String str = r.toString().substring(1);
                    int index = str.indexOf('/');
                    if (index >= 0) {
                        str = str.substring(index + 1);
                    }
                    CounterActor actorProxy = ActorProxyBase.create(new ActorId(str), serviceUri, CounterActor.class);
                    t.sendResponseHeaders(200, 0);
                    OutputStream os = t.getResponseBody();
                    String result = String.format("Actor:%s  ActorId:%s CounterValue:%d", str, ActorExtensions.getActorId(actorProxy).toString(), actorProxy.getCountAsync().get());
                    os.write(result.getBytes("UTF-8"));
                    os.close();
                } catch (Exception e) {
                    logger.log(Level.WARNING, null, e);
                }
            }
        }
        );
        
        server.setExecutor(null);
        server.start();
    }

    @Override
    public CompletableFuture<String> openAsync() {
        this.start();
        String publishUri = String.format("http://%s:%d/", this.serviceContext.nodeContext().getIpAddressOrFQDN(), port);
        return CompletableFuture.completedFuture(publishUri);
    }

    @Override
    public CompletableFuture<?> closeAsync() {
        this.server.stop(0);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void abort() {
        this.server.stop(0);
    }
}
