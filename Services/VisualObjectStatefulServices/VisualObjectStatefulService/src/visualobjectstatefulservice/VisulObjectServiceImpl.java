// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectstatefulservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import microsoft.servicefabric.data.ReliableStateManager;
import microsoft.servicefabric.data.Transaction;
import microsoft.servicefabric.data.collections.ReliableHashMap;
import microsoft.servicefabric.data.utilities.AsyncEnumeration;
import microsoft.servicefabric.data.utilities.KeyValuePair;
import microsoft.servicefabric.services.runtime.StatefulService;
import system.fabric.CancellationToken;
import system.fabric.StatefulServiceContext;
import visualobjectcommon.VisualObject;
import visualobjectcommon.VisualObjectService;
import microsoft.servicefabric.services.communication.runtime.ServiceReplicaListener;
import microsoft.servicefabric.services.remoting.fabrictransport.runtime.FabricTransportServiceRemotingListener;;


public class VisulObjectServiceImpl extends StatefulService implements VisualObjectService {
        private ReliableStateManager stateManager;
        private final String hashMapName = "visualObjects";
        private final Object mutex = new Object();
        private ReliableHashMap<String, VisualObject> map = null;
        protected VisulObjectServiceImpl (StatefulServiceContext statefulServiceContext) {
            super (statefulServiceContext);
            this.stateManager = this.getReliableStateManager();
            
        }
    
    @Override
    public CompletableFuture<String> getStateAsJsonAsync(VisualObject objectId) {
    	return moveObjectAsync(objectId);
    }    
    protected CompletableFuture<String> moveObjectAsync(VisualObject objectId) {
    	if (map == null) {
    		synchronized (mutex) {
    			if(map == null) {
    				try {
    					this.map = this.stateManager.<String, VisualObject>getOrAddReliableHashMapAsync(hashMapName).get();
    				} catch (InterruptedException | ExecutionException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}
    		}
    	}
    	
       	Transaction tx = stateManager.createTransaction();
    	return map.getAsync(tx, objectId.name()).thenCompose(cv -> {
    		//System.out.println("_________ Enter getAsync call, " + objectId.name());
    		if(cv.hasValue()){
    			VisualObject objectId1 = cv.getValue();
    			objectId1.move();
    			return map.replaceAsync(tx, objectId1.name(), objectId1).handle((r, e) -> {
            		//System.out.println("_________ After replaceAsync call, " + objectId.name() + objectId.toString());
            		String jsonString = objectId1.toJson();
            		try {
    					tx.commitAsync().get();
    					tx.close();
    				} catch (Exception e1) {
    					e1.printStackTrace();
    				}
            		return jsonString;
            	});
    			
    		} else {
        			return map.putAsync(tx, objectId.name(), objectId).handle((r, e) -> {
	        			//System.out.println("_________ After putAsync call, " + objectId.name());
	        			String jsonString = objectId.toJson();
	        			try {
							tx.commitAsync().get();
							tx.close();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						return jsonString;
        			});
				
    		}
    	});
    	
    }

    @Override 
    public List<ServiceReplicaListener> createServiceReplicaListeners()
    {
    	List<ServiceReplicaListener> listenerList = new ArrayList<>();
    	ServiceReplicaListener listener = new ServiceReplicaListener(context -> new FabricTransportServiceRemotingListener(context, this));
    	listenerList.add(listener);
    	return listenerList; 
    }
    
}
