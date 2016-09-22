// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package counterclient;

import counterinterface.CounterActor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import microsoft.servicefabric.actors.ActorExtensions;
import microsoft.servicefabric.actors.ActorId;
import microsoft.servicefabric.actors.ActorProxyBase;

public class CounterClient {

    static String actorName = "Actor1";
    static String actorCounterServiceName = "fabric:/CounterActorApplication/CounterActorService";
    public static void main(String[] args) throws URISyntaxException, InterruptedException, ExecutionException {
        CounterActor actorProxy = ActorProxyBase.create(new ActorId(actorName), new URI(actorCounterServiceName), CounterActor.class);
        int count = actorProxy.getCountAsync().get();
        System.out.println("From Actor:" + ActorExtensions.getActorId(actorProxy) + " " + count);
    }
 
}
