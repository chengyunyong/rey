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
import io.xream.internal.util.StringUtil;
import io.xream.rey.api.ClientRestTemplate;
import io.xream.rey.api.ReyTemplate;
import io.xream.rey.api.exceptionhandler.ClientExceptionProcessSupportable;
import io.xream.rey.config.ReyConfigurable;
import io.xream.rey.proto.ReyResponse;

import java.util.List;

/**
 * @author Sim
 */
public class HttpClientBackendImpl implements ClientBackend {

    private ReyConfigurable reyConfigurable;

    private ClientExceptionProcessSupportable clientExceptionProcessSupportable;

    private ReyTemplate reyTemplate;

    private ClientRestTemplate clientRestTemplate;

    public HttpClientBackendImpl(ClientRestTemplate wrapper) {
        this.clientRestTemplate = wrapper;
    }


    public void setClientExceptionProcessSupportable(ClientExceptionProcessSupportable clientExceptionProcessSupportable) {
        this.clientExceptionProcessSupportable = clientExceptionProcessSupportable;
    }

    public void setClientRestTemplate(ClientRestTemplate restTemplate) {
        this.clientRestTemplate = restTemplate;
    }

    public void setReyTemplate( ReyTemplate reyTemplate) {
        this.reyTemplate = reyTemplate;
    }

    public void setReyConfigurable(ReyConfigurable reyConfigurable) {
        this.reyConfigurable = reyConfigurable;
    }


    @Override
    public ClientExceptionProcessSupportable clientExceptionHandler() {
        return this.clientExceptionProcessSupportable;
    }

    @Override
    public ReyTemplate reyTemplate() {
        return this.reyTemplate;
    }

    @Override
    public ReyConfigurable reyConfigurable() {
        return this.reyConfigurable;
    }

    @Override
    public ReyResponse handle(R r) {
        return this.clientRestTemplate.exchange(r.getUrl(),
                r.getArg(),
                r.getHeaders(),
                r.getRequestMethod());
    }


    @Override
    public Object toObject(Class<?> returnType, Class<?> geneType, String result) {

        if (StringUtil.isNullOrEmpty(result))
            return null;

        if (returnType == null || returnType == void.class) {
            return null;
        }

        if (returnType == Object.class)
            return result;

        if (returnType == List.class){
            return JsonX.toList(result,geneType);
        }

        return JsonX.toObject(result, returnType);
    }


}
