// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package echoserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServer {

    private static final Logger logger = Logger.getLogger(HttpServer.class.getName());

    private com.sun.net.httpserver.HttpServer server;
    Random rand = new Random();
    private int port;
    private String appVersion = "Version 2.0";

    public HttpServer(int port) {
        this.port = port;
    }

    public void startServer() throws IOException {
        try {
            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);
            logger.log(Level.FINE, "Started HTTP Server on port {0}", port);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception in creating HttpServer: {0}", e.toString());
            throw e;
        }
        server.createContext("/getMessage", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) throws IOException {
                byte[] buffer = null;
                if (t.getRequestURI().getQuery() != null) {
                    buffer = getMessage("Hello " + t.getRequestURI().getQuery());
                } else {
                    buffer = getMessage("Hello World");
                }
                t.sendResponseHeaders(200, buffer.length);
                OutputStream os = t.getResponseBody();
                os.write(buffer);
                os.close();
            }
        });
        server.setExecutor(null);
        server.start();
    }

    private byte[] getMessage(String str) throws UnsupportedEncodingException {
        return ("<h1> [" + appVersion + "] :: " + str + " !!! </h1>").getBytes("UTF-8");
    }

    public void stopServer() {
        server.stop(10);
    }
}
