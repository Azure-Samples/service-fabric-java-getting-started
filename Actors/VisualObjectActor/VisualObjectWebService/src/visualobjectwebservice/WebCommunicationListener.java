// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectwebservice;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import microsoft.servicefabric.services.communication.runtime.CommunicationListener;
import microsoft.servicefabric.services.runtime.StatelessServiceContext;

public class WebCommunicationListener implements CommunicationListener {

    private final VisualObjectsBox visualObjectsBox;
    private WebSocketApp webSocketApp;
    private int port;
    private StatelessServiceContext context;
    
    public WebCommunicationListener(VisualObjectsBox visualObjectsBox, StatelessServiceContext context, int port) {
        this.visualObjectsBox = visualObjectsBox;
        this.port = port;
        this.context = context;
    }

    @Override
    public CompletableFuture<String> openAsync() {
        this.webSocketApp = new WebSocketApp(this.visualObjectsBox, this.port);
        try {
            this.webSocketApp.start();
        } catch (IOException ex) {
            Logger.getLogger(WebCommunicationListener.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

        return CompletableFuture.supplyAsync(() -> {
            return String.format("http://%s:%d", this.context.nodeContext().getIpAddressOrFQDN(), port);
        });
    }

    @Override
    public CompletableFuture<?> closeAsync() {
        try
        {
            if (this.webSocketApp != null)
                this.webSocketApp.close();
        }
        catch (Exception ex) {}
        return CompletableFuture.supplyAsync(() -> true);
    }

    @Override
    public void abort() {
        try
        {
            if (this.webSocketApp != null)
                this.webSocketApp.close();
        }
        catch (Exception ex) {}
    }
}
