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
package io.xream.rey.api;

import io.xream.rey.proto.ReyResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author Sim
 */
public interface ClientRestTemplate {

    void wrap(Object resetTemplate);
    void headerInterceptor(ClientHeaderInterceptor interceptor);
    List<ClientHeaderInterceptor> clientHeaderInterceptors();
    ReyResponse exchange(String url, Object request, HttpHeaders headers, RequestMethod httpMethod);

    default void handleHeaders(HttpHeaders headers,RequestMethod requestMethod) {

        for (ClientHeaderInterceptor headerInterceptor : clientHeaderInterceptors()) {
            headerInterceptor.apply(headers);
        }

        if (headers.getContentType() == null
                && (
                requestMethod == RequestMethod.POST
                        || requestMethod == RequestMethod.PUT
                        || requestMethod == RequestMethod.DELETE
                        || requestMethod == RequestMethod.PATCH
        )) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
    }
}
