// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectwebservice;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

import microsoft.servicefabric.actors.ActorId;
import microsoft.servicefabric.actors.ActorProxyBase;
import visualobjectcommon.VisualObjectActor;

public class VisualObjectsBoxImpl implements VisualObjectsBox {

    private final URI serviceUri;
    private final ArrayList<ActorId> objectIds;

    public VisualObjectsBoxImpl(URI uri, int numberOfObjects) {
        this.serviceUri = uri;
        this.objectIds = new ArrayList<>();
        for (int i = 0; i < numberOfObjects; ++i) {
            this.objectIds.add(new ActorId(String.format(Locale.US, "Visual Object # %d", i)));
        }
    }

    @Override
    public CompletableFuture<String> getObjectsAsync() {
        List<CompletableFuture<String>> tasks = new ArrayList<>();
        for (ActorId id : this.objectIds) {
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

    private CompletableFuture<String> getObjectAsync(ActorId objectId) {
        VisualObjectActor actorProxy = ActorProxyBase.create(objectId, this.serviceUri, VisualObjectActor.class);
        try {
            CompletableFuture<String> result = actorProxy.getStateAsJsonAsync();
            return result;
        } catch (Exception e) {
            // ignore the exceptions
            return CompletableFuture.supplyAsync(() -> {
                return "";
            });
        }
    }
}
