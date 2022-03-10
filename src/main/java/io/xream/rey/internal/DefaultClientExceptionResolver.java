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

import io.xream.internal.util.JsonX;
import io.xream.rey.api.CircuitbreakerExceptionHandler;
import io.xream.rey.api.ClientExceptionResolver;
import io.xream.rey.api.FallbackHandler;
import io.xream.rey.api.ReyHttpStatus;
import io.xream.rey.exception.ReyInternalException;
import io.xream.rey.exception.ReyRuntimeException;
import io.xream.rey.proto.RemoteExceptionProto;
import io.xream.rey.proto.RemoteExceptionUnknown;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * @author Sim
 */
public class DefaultClientExceptionResolver implements ClientExceptionResolver {

    private CircuitbreakerExceptionHandler circuitbreakerExceptionHandler;
    private FallbackHandler fallbackHandler;

    public void setCircuitbreakerExceptionHandler(CircuitbreakerExceptionHandler handler) {
        this.circuitbreakerExceptionHandler = handler;
    }

    public void setFallbackHandler(FallbackHandler fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
    }

    @Override
    public void convertNot200ToException(int status, String uri, String response) throws ReyInternalException{
        if (status == ReyHttpStatus.TO_CLIENT.getStatus()) {
            RemoteExceptionProto proto = JsonX.toObject(response, RemoteExceptionProto.class);
            throw ReyInternalException.createToClient(uri, proto);
        }
    }

    @Override
    public void handleException(ReyInternalException rie) throws ReyInternalException{

        Throwable e= rie.getCause();

        this.circuitbreakerExceptionHandler().handle(rie);

        final String uri = rie.getUri();

        if (e instanceof ResourceAccessException){
            ResourceAccessException rae = (ResourceAccessException)e;
            Throwable t = rae.getRootCause();
            String str = rae.getLocalizedMessage();
            String[] arr = str.split(";");
            final String message = arr[0];
            if (t instanceof ConnectException) {
                throw ReyInternalException.create(-1 ,message, ReyExceptionUtil.getStack(e),null,uri);
            }else if (t instanceof SocketTimeoutException) {
                throw ReyInternalException.create(-2 ,message,ReyExceptionUtil.getStack(e),null,uri);
            }
        }else if (e instanceof HttpClientErrorException){
            HttpClientErrorException ee = (HttpClientErrorException)e;
            String str = ee.getLocalizedMessage();
            str = adaptJson(str);
            RemoteExceptionUnknown unknown = JsonX.toObject(str,RemoteExceptionUnknown.class);
            List<RemoteExceptionProto.ExceptionTrace> traces = unknown.getExceptionTraces();
            if (traces == null) { //unknown
                String stack = ReyExceptionUtil.getStack(e);
                throw ReyInternalException.create(
                        ee.getStatusCode().value(),
                        unknown.getError(),
                        stack,
                        null,
                        unknown.getPath());
            }else {
                throw ReyInternalException.createToClient(ee.getStatusCode().value(),
                        unknown.getError(),
                        uri,
                        traces
                        );
            }
        }else if (e instanceof HttpServerErrorException) {
            HttpServerErrorException ee = (HttpServerErrorException)e;
            ee.printStackTrace();
            String str = ee.getLocalizedMessage();
            str = adaptJson(str);
            RemoteExceptionUnknown unknown = JsonX.toObject(str,RemoteExceptionUnknown.class);
            List<RemoteExceptionProto.ExceptionTrace> traces = unknown.getExceptionTraces();

            if (traces == null) { //unknown
                String stack = ReyExceptionUtil.getStack(e);
                throw ReyInternalException.create(
                        ee.getStatusCode().value(),
                        unknown.getError(),
                        stack,
                        null,
                        unknown.getPath());
            }else {
                throw ReyInternalException.createToClient(ee.getStatusCode().value(),
                        unknown.getError(),
                        uri,
                        traces
                );
            }

        }else if (e instanceof IllegalArgumentException) {
            throw ReyInternalException.create(
                    400,
                    e.getMessage(),
                    ReyExceptionUtil.getStack(e),
                    null,
                    uri);
        }

        throw new ReyRuntimeException(e);
    }

    @Override
    public FallbackHandler fallbackHandler() {
        return this.fallbackHandler;
    }

    @Override
    public CircuitbreakerExceptionHandler circuitbreakerExceptionHandler() {
        return this.circuitbreakerExceptionHandler;
    }

}
