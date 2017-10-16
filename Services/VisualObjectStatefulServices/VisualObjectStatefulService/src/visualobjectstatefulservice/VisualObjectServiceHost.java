// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectstatefulservice;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import microsoft.servicefabric.services.runtime.ServiceRuntime;

public class VisualObjectServiceHost {

    private static Logger logger = Logger.getLogger(VisualObjectServiceHost.class.getName());
    
    public static void main(String[] args) throws Exception {
        try {
        	ServiceRuntime.registerStatefulServiceAsync("VisualObjects.StatefulServiceType", (context) -> new VisulObjectServiceImpl(context), Duration.ofSeconds(10));
            logger.log(Level.INFO, "Registered stateful service of type VisualObjects.StatefulServiceType. ");
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception in registration: {0}", e.toString());
            throw e;
        }
    }

}
