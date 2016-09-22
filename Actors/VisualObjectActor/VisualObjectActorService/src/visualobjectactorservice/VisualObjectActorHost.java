// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectactorservice;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import microsoft.servicefabric.actors.ActorRuntime;

public class VisualObjectActorHost {

    private static Logger logger = Logger.getLogger(VisualObjectActorHost.class.getName());
    
    public static void main(String[] args) throws Exception {
        try {
            ActorRuntime.registerActorAsync(VisualObjectActorImpl.class, Duration.ofSeconds(10));      
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception in registration: {0}", e.toString());
            throw e;
        }
    }

}
