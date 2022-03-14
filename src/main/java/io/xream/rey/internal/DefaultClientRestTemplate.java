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
import io.xream.rey.api.ClientHeaderInterceptor;
import io.xream.rey.api.ClientRestTemplate;
import io.xream.rey.exception.ReyInternalException;
import io.xream.rey.proto.ReyResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sim
 */
public class DefaultClientRestTemplate implements ClientRestTemplate {

    private RestTemplate restTemplate;

    private List<ClientHeaderInterceptor> clientHeaderInterceptorList = new ArrayList<>();


    @Override
    public void wrap(Object resetTemplate) {
        this.restTemplate = (RestTemplate) resetTemplate;
    }

    @Override
    public void headerInterceptor(ClientHeaderInterceptor interceptor) {
        this.clientHeaderInterceptorList.add(interceptor);
    }

    @Override
    public List<ClientHeaderInterceptor> clientHeaderInterceptors() {
        return this.clientHeaderInterceptorList;
    }

    @Override
    public ReyResponse exchange(String url, Object request, HttpHeaders headers, RequestMethod requestMethod) {
        handleHeaders(headers,requestMethod);
        ReyResponse result = null;
        switch (requestMethod) {
            case GET:
                result = this.execute(url, request, headers, HttpMethod.GET);
                break;
            case PUT:
                result = this.execute(url, request, headers, HttpMethod.PUT);
                break;
            case DELETE:
                result = this.execute(url, request, headers, HttpMethod.DELETE);
                break;
            default:
                result = this.execute(url, request, headers, HttpMethod.POST);
        }

        return result;
    }


    private ReyResponse execute(String url, Object request, HttpHeaders headers, HttpMethod method) {

        String json = request == null ? "" : JsonX.toJson(request);

        if (this.restTemplate == null)
            throw new NullPointerException(RestTemplate.class.getName());

        try {
            ResponseEntity<String> re = restTemplate.exchange(url, method, new HttpEntity<>(json, headers), String.class);

            ReyResponse reyResponse = new ReyResponse();
            reyResponse.setBody(re.getBody());
            reyResponse.setStatus(re.getStatusCodeValue());
            reyResponse.setUri(url);
            return reyResponse;
        } catch (Throwable throwable) {
            ReyInternalException rie = new ReyInternalException(throwable);
            rie.setUri(url);
            throw rie;
        }
    }


}
