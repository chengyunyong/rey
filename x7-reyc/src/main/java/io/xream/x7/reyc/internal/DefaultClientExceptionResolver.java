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
package io.xream.x7.reyc.internal;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.xream.x7.base.api.BackendService;
import io.xream.x7.base.api.ReyHttpStatus;
import io.xream.x7.base.exception.BusyException;
import io.xream.x7.base.exception.RemoteBizException;
import io.xream.x7.base.exception.ReyException;
import io.xream.x7.base.util.ExceptionUtil;
import io.xream.x7.base.util.JsonX;
import io.xream.x7.reyc.api.ClientExceptionResolver;
import org.apache.commons.collections.MapUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * @author Sim
 */
public class DefaultClientExceptionResolver implements ClientExceptionResolver {
    @Override
    public boolean ignore222() {
        return false;
    }

    @Override
    public String handleException(Throwable e, BackendService backendService) {

        if (e instanceof CallNotPermittedException) {
            Object obj = backendService.fallback();
            throw new BusyException(obj == null ? null : obj.toString());
        }

        if (e instanceof ResourceAccessException){
            backendService.fallback();
            ResourceAccessException rae = (ResourceAccessException)e;
            Throwable t = rae.getRootCause();
            String str = rae.getLocalizedMessage();
            String[] arr = str.split(";");
            final String message = arr[0];
            if (t instanceof ConnectException) {
                throw ReyException.create(ReyHttpStatus.TO_CLIENT, -1 ,message, ExceptionUtil.getMessage(e),null);
            }else if (t instanceof SocketTimeoutException) {
                throw ReyException.create(ReyHttpStatus.TO_CLIENT, -2 ,message,ExceptionUtil.getMessage(e),null);
            }
        }else if (e instanceof HttpClientErrorException){
            backendService.fallback();
            HttpClientErrorException ee = (HttpClientErrorException)e;
            String str = ee.getLocalizedMessage();
            str = str.split(": ")[1].trim();
            str = str.replace("[","");
            str = str.replace("]","");
            Map<String,Object> map = JsonX.toMap(str);
            String message = MapUtils.getString(map, "path");
            throw ReyException.create(ReyHttpStatus.TO_CLIENT, ee.getStatusCode().value() ,message,ExceptionUtil.getMessage(e),null);
        }else if (e instanceof HttpServerErrorException) {
            HttpServerErrorException hse = (HttpServerErrorException)e;
            String str = hse.getLocalizedMessage();
            str = str.split(": ")[1].trim();
            str = str.replace("[","");
            str = str.replace("]","");
            if (!str.endsWith("\"}")) {
                str += "\"}";
            }
            Map<String,Object> map = JsonX.toMap(str);
            String stack = MapUtils.getString(map,"stack");
            if (stack == null) {
                stack = ExceptionUtil.getMessage(e);
            }
            throw ReyException.create(ReyHttpStatus.INTERNAL_SERVER_ERROR, hse.getStatusCode().value() ,
                    MapUtils.getString(map,"message"),
                    stack,
                    MapUtils.getString(map,"traceId")
            );
        }

        throw new RemoteBizException(e);
    }
}
