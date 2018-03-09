package statelessservice;

import java.time.Duration;
import java.util.logging.Logger;
import java.util.logging.Level;

import microsoft.servicefabric.services.runtime.ServiceRuntime;

public class VotingWebServiceHost {

    private static final Logger logger = Logger.getLogger(VotingWebServiceHost.class.getName());

    public static void main(String[] args) throws Exception{
        try {
            ServiceRuntime.registerStatelessServiceAsync("VotingWebType", (context)-> new VotingWebService(), Duration.ofSeconds(10));
            logger.log(Level.INFO, "Registered stateless service of type VotingWebType");
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception occurred", ex);
            throw ex;
        }
    }
}
