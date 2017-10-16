// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectwebservice;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

import microsoft.servicefabric.services.client.ServicePartitionKey;
import microsoft.servicefabric.services.communication.client.TargetReplicaSelector;
import microsoft.servicefabric.services.remoting.client.ServiceProxyBase;
import visualobjectcommon.VisualObject;
import visualobjectcommon.VisualObjectService;

public class VisualObjectsBoxImpl implements VisualObjectsBox {

    private final URI serviceUri;
    private final ArrayList<VisualObject> objectIds;

    public VisualObjectsBoxImpl(URI uri, int numberOfObjects) {
        this.serviceUri = uri;
        this.objectIds = new ArrayList<>();
        for (int i = 0; i < numberOfObjects; ++i) {
        	String objectName = String.format(Locale.US, "VisualObject#%d", i);
            this.objectIds.add(VisualObject.createRandom(objectName,
                    new Random(objectName.hashCode())));
        }
    }

    @Override
    public CompletableFuture<String> getObjectsAsync() {
        List<CompletableFuture<String>> tasks = new ArrayList<>();
        for (VisualObject id : this.objectIds) {
            tasks.add(getObjectAsync(id));
        }

        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[this.objectIds.size()])).thenApply((x) -> {
            StringJoiner sj = new StringJoiner(",", "[", "]");
            for (CompletableFuture<String> t : tasks) {
                try {
                    sj.add(t.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return sj.toString();
        });
    }

    private CompletableFuture<String> getObjectAsync(VisualObject objectId) {
    	VisualObjectService serviceProxy = ServiceProxyBase.create(VisualObjectService.class, this.serviceUri, new ServicePartitionKey(0L), TargetReplicaSelector.DEFAULT, null);
    	try {
            CompletableFuture<String> result = serviceProxy.getStateAsJsonAsync(objectId);
            return result;
        } catch (Exception e) {
            // ignore the exceptions
            return CompletableFuture.supplyAsync(() -> {
                return "";
            });
        }
    }
}
