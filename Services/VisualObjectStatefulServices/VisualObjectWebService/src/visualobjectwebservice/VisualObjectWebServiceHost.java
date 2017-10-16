// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectwebservice;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import microsoft.servicefabric.services.runtime.ServiceRuntime;

public class VisualObjectWebServiceHost {

    private static Logger logger = Logger.getLogger(VisualObjectWebServiceHost.class.getName());
    
    public static void main(String[] args) throws Exception {
        try {
            ServiceRuntime.registerStatelessServiceAsync("VisualObjects.WebServiceType", (context) -> new VisualObjectWebService(), Duration.ofSeconds(10));
            logger.log(Level.INFO, "Registered stateless service type VisualObjects.WebServiceType.");
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception in registration: {0}", ex.toString());
            throw ex;
        }
    }
}
