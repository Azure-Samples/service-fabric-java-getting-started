// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package watchdog;

import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

import microsoft.servicefabric.services.communication.client.ExceptionHandler;
import microsoft.servicefabric.services.communication.client.ExceptionHandlingResult;
import microsoft.servicefabric.services.communication.client.ExceptionHandlingRetryResult;
import microsoft.servicefabric.services.communication.client.ExceptionHandlingThrowResult;
import microsoft.servicefabric.services.communication.client.ExceptionInformation;
import microsoft.servicefabric.services.communication.client.OperationRetrySettings;

public class CommunicationExceptionHandler implements ExceptionHandler {

    @Override
    public ExceptionHandlingResult handleException(
            ExceptionInformation exceptionInformation,
            OperationRetrySettings retrySettings) {        

        Exception ex = exceptionInformation.exception();
        if (ex instanceof CompletionException) {
            Throwable t = ((CompletionException) ex).getCause();
            while (t instanceof RuntimeException)
                t = ((RuntimeException) t).getCause();

            if (t instanceof ConnectException) {
                ExceptionHandlingRetryResult exceptionHandlingRetryResult =
                        new ExceptionHandlingRetryResult(exceptionInformation.exception(), false, retrySettings
                                .maxRetryBackoffIntervalOnNonTransientErrors(), retrySettings.defaultMaxRetryCount());
                return exceptionHandlingRetryResult;
            }
        }

        ExceptionHandlingThrowResult result = new ExceptionHandlingThrowResult();
        result.setExceptionToThrow(ex);
        return result;
    }
}
