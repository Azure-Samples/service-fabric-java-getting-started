// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectwebservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebSocketApp implements AutoCloseable {

    private HttpServer server;
    private final VisualObjectsBox visualObjectBox;
    int port;

    public WebSocketApp(VisualObjectsBox visualObjectBox, int port) {
        this.visualObjectBox = visualObjectBox;
        this.port = port;
    }

    public void start() throws IOException {
        server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(this.port), 0);
        server.createContext("/nodes.json", new HttpHandler() {

            @Override
            public void handle(HttpExchange t) {
                try {
                    byte[] buffer = visualObjectBox.getObjectsAsync().get().getBytes("UTF-8");
                    t.sendResponseHeaders(200, buffer.length);
                    OutputStream os = t.getResponseBody();
                    os.write(buffer);
                    os.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        });
        server.createContext("/", new HttpHandler() {

            @Override
            public void handle(HttpExchange t) throws IOException {
                StringBuilder sb = new StringBuilder();
                BufferedReader br;
                try {
                    URI r = t.getRequestURI();
                    String str = r.toString().substring(1);
                    String currentPath = Paths.get("").toAbsolutePath().toString();
                    String wwwrootstr = new File(currentPath, "wwwroot").toString();
                    String filestr = new File(wwwrootstr, str).toString();
                    br = new BufferedReader(new FileReader(filestr));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }

                    br.close();
                    byte[] buffer = sb.toString().getBytes("UTF-8");
                    t.sendResponseHeaders(200, buffer.length);
                    OutputStream os = t.getResponseBody();
                    os.write(buffer);
                    os.close();
                } catch (FileNotFoundException e) {
                    // Ignore exception due to missing favicon.ico
                }
            }
        });
        server.setExecutor(null);
        server.start();
    }

    @Override
    public void close() {
        if (server != null)
        {
            server.stop(0);
            server = null;
        }
    }
}
