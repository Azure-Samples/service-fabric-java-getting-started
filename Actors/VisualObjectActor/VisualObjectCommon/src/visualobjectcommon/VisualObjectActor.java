// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectcommon;

import java.util.concurrent.CompletableFuture;
import microsoft.servicefabric.actors.Actor;

public interface VisualObjectActor extends Actor {

    CompletableFuture<String> getStateAsJsonAsync();
}
