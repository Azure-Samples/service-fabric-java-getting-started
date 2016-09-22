// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package counterinterface;

import java.util.concurrent.CompletableFuture;
import microsoft.servicefabric.actors.Actor;

public interface CounterActor extends Actor {
  
    CompletableFuture<Integer> getCountAsync();

    CompletableFuture<?> setCountAsync(int count);
}
