// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package watchdog;

import microsoft.servicefabric.services.communication.client.CommunicationClient;
import system.fabric.ResolvedServiceEndpoint;
import system.fabric.ResolvedServicePartition;

public class HttpCommunicationClient implements CommunicationClient {

    private String endPointAddress;
    private ResolvedServicePartition rsp;
    private String listenerName;
    private ResolvedServiceEndpoint rse;
    
    public String endPointAddress() {
        return this.endPointAddress;
    }

    public HttpCommunicationClient(String endPoint) {
        this.endPointAddress = endPoint;
    }

    @Override
    public void setResolvedServicePartition(ResolvedServicePartition servicePartition) {
        this.rsp = servicePartition;
    }

    @Override
    public ResolvedServicePartition resolvedServicePartition() {
        return this.rsp;
    }

    @Override
    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
        
    }

    @Override
    public String listenerName() {
        return this.listenerName;
    }

    @Override
    public ResolvedServiceEndpoint endPoint() {
        return this.rse;
    }

    @Override
    public void setEndPoint(ResolvedServiceEndpoint endPoint) {
        this.rse = endPoint;
    }
}
