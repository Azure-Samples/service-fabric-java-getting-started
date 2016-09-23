// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package watchdog;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileNotFoundException;

import microsoft.servicefabric.services.communication.client.ServicePartitionClientImpl;
import microsoft.servicefabric.services.communication.runtime.CommunicationListener;
import microsoft.servicefabric.services.communication.client.ExceptionHandler;
import microsoft.servicefabric.services.runtime.StatelessServiceContext;

public class HttpCommunicationListener implements CommunicationListener {

    private static final Logger logger = Logger.getLogger(HttpCommunicationListener.class.getName());

    private com.sun.net.httpserver.HttpServer server;
    private ServicePartitionClientImpl<HttpCommunicationClient> client;
    private StatelessServiceContext context;
    private final int port;

    public HttpCommunicationListener(URI serviceName, StatelessServiceContext context, int port) {
        List<ExceptionHandler> exceptionHandlers = new ArrayList<ExceptionHandler>(){{
            add(new CommunicationExceptionHandler());
            }}; 
        this.client = new ServicePartitionClientImpl<HttpCommunicationClient>(new HttpCommunicationClientFactory(null, exceptionHandlers), serviceName);
        this.context = context;
        this.port = port;
    }

    public void start() {
        try {
            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(this.port), 0);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) {
                try {
                    URI r = t.getRequestURI();
                    String method = t.getRequestMethod();
                    String str = r.toString().substring(1);

                    t.sendResponseHeaders(200, 0);
                    OutputStream os = t.getResponseBody();
                    client.invokeWithRetryAsync((c) -> {
                        CompletableFuture<Boolean> b = new CompletableFuture<>();
                        String address = c.endPointAddress();
                        int index = address.indexOf('/', 7);
                        if (index != -1) {
                            address = address.substring(0, index);
                        }

                        address = address + "/" + str;
                        URL clientUrl;
                        try {
                            clientUrl = new URL(address);
                            HttpURLConnection conn = (HttpURLConnection) clientUrl.openConnection();
                            conn.setRequestMethod(method);
                            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String line;
                            while ((line = rd.readLine()) != null) {
                                String str1 = "\n";
                                os.write(line.getBytes("UTF-8"));
                                os.write(str1.getBytes("UTF-8"));
                            }
                            rd.close();
                            b.complete(true);
                        } catch (FileNotFoundException ex) {
                            logger.log(Level.WARNING, null, ex);
                            b.complete(true);
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE, null, ex);
                            b.completeExceptionally(ex);
                        }

                        return b;
                    }).get();
                    os.close();
                } catch (Exception e) {
                    logger.log(Level.WARNING, null, e);
                    // Let the handle loop continue
                }
            }
        }
        );
        
        server.setExecutor(null);
        server.start();
    }

    private void stop() {
        if (null != server)
            server.stop(0);
    }

    @Override
    public CompletableFuture<String> openAsync() {
        this.start();
        String publishUri = String.format("http://%s:%d/", this.context.nodeContext().getIpAddressOrFQDN(), port);
        return CompletableFuture.completedFuture(publishUri);
    }

    @Override
    public CompletableFuture<?> closeAsync() {
        this.stop();
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void abort() {
        this.stop();
    }
}
