// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package echoserver;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import microsoft.servicefabric.services.runtime.ServiceRuntime;

public class EchoServerServiceHost {

    private static final Logger logger = Logger.getLogger(EchoServerServiceHost.class.getName());

    public static void main(String[] args) throws Exception {
        try {
            ServiceRuntime.registerStatelessServiceAsync("EchoServerType", (context) -> new EchoServerService(), Duration.ofSeconds(10));
            logger.log(Level.INFO, "Registered stateless service type EchoServerType.");
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception in registration: {0}", ex.toString());
            throw ex;
        }
    }
}
