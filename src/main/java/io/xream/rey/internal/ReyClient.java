/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.xream.rey.internal;

import io.xream.internal.util.ExceptionUtil;
import io.xream.rey.api.BackendService;
import io.xream.rey.api.ClientExceptionProcessSupportable;
import io.xream.rey.api.ReyTemplate;
import io.xream.rey.config.ReyConfigurable;
import io.xream.rey.exception.ReyInternalException;
import io.xream.rey.fallback.Fallback;
import io.xream.rey.proto.ReyResponse;


/**
 * @author Sim
 */
public interface ReyClient extends Fallback {

    ReyTemplate reyTemplate();

    ReyConfigurable reyConfigurable();

    ClientExceptionProcessSupportable clientExceptionHandler();

//    Object service(BackendDecoration backendDecoration, BackendService<ReyResponse> backendService) throws ReyInternalException;

    default Object service(BackendDecoration backendDecoration, BackendService<ReyResponse> backendService) throws ReyInternalException {

        Object result = null;
        try {
            if (backendDecoration == null
                    || this.reyConfigurable() == null
                    || ! this.reyConfigurable().isCircuitbreakerEnabled(backendDecoration.getConfigName())) {
                result = backendService.handle();
            } else {
                result = this.reyTemplate().support(
                        backendDecoration.getServiceName(),
                        backendDecoration.getConfigName(), backendDecoration.isRetry(),
                        backendService
                );
            }
        }catch (ReyInternalException e) {
            try {
                this.clientExceptionHandler().callNotPermittedExceptionConverter().convertIfCallNotPermitted(e);
                this.clientExceptionHandler().respondedExceptionConverter().convertRespondedException(e);
            }catch (ReyInternalException rie) {

                if (! this.clientExceptionHandler()
                        .fallbackDeterminate().isNotRequireFallback(rie.status())) {
                    try {
                        return backendService.fallback(rie);
                    }catch (Throwable t) {
                        if (t instanceof ReyInternalException)
                            throw rie;
                        rie.setErrorOnFallback(ExceptionUtil.getMessage(t));
                        throw rie;
                    }
                }
                throw rie;
            }
        }catch (Exception e) {
            throw e;
        }

        if (result == null)
            return null;
        ReyResponse reyResponse = (ReyResponse) result;
        final int status = reyResponse.getStatus();
        final String body = reyResponse.getBody();
        final String path = reyResponse.getUri();

        //FIXME
        this.clientExceptionHandler().respondedExceptionConverter().convertNot200ToException(status,path,body);

        return result;
    }
}
