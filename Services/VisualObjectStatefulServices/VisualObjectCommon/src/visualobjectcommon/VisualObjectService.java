// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectcommon;

import java.util.concurrent.CompletableFuture;

import microsoft.servicefabric.services.remoting.Service;

public interface VisualObjectService extends Service {
    CompletableFuture<String> getStateAsJsonAsync(VisualObject objectName);
}
