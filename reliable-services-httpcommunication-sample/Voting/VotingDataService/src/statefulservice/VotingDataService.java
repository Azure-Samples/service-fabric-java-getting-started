package statefulservice;

import java.util.ArrayList;
import java.util.List;

import microsoft.servicefabric.services.communication.runtime.ServiceReplicaListener;
import microsoft.servicefabric.services.runtime.StatefulService;
import system.fabric.StatefulServiceContext;

public class VotingDataService extends StatefulService {

    protected VotingDataService (StatefulServiceContext statefulServiceContext) {
        super (statefulServiceContext);
    }

    @Override
    protected List<ServiceReplicaListener> createServiceReplicaListeners() {
        ServiceReplicaListener listener1 = new ServiceReplicaListener(initParams -> {
            return new HttpCommunicationListener(this.getServiceContext(), 
                    this.getReliableStateManager());
	    }, "Listener1");
	    List<ServiceReplicaListener> listenerList = new ArrayList<>();
	    listenerList.add(listener1);
	    return listenerList;
    }
    
}