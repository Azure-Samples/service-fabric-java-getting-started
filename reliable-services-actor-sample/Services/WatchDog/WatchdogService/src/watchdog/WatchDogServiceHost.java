// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package watchdog;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import microsoft.servicefabric.services.runtime.ServiceRuntime;

public class WatchDogServiceHost {

    private static final Logger logger = Logger.getLogger(WatchDogServiceHost.class.getName());

    public static void main(String[] args) throws Exception {
        try {
            ServiceRuntime.registerStatelessServiceAsync("WatchDogType", (context) -> new WatchDogService(), Duration.ofSeconds(10));
            logger.log(Level.INFO, "Registered stateless service type WatchDogType.");
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception in registration: {0}", ex.toString());
            throw ex;
        }
    }
}
