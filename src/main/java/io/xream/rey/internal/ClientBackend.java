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

import io.xream.rey.api.BackendService;
import io.xream.rey.exception.MismatchedReturnTypeException;
import io.xream.rey.fallback.FallbacKey;
import io.xream.rey.proto.ReyResponse;

import java.lang.reflect.Method;

/**
 * @author Sim
 */
public interface ClientBackend extends ReyClient {

    Object toObject(Class<?> returnType, Class<?> geneType, String result);

    ReyResponse handle(R r);

    default Object invoke(
            Class proxyType,
            Method proxyMethod,
            Object[] proxyArgs,
            BackendDecoration bd, ClientBackend clientBackend) {
        R r = R.build(proxyType, proxyMethod, proxyArgs);

        ClientBackendLogger.log(proxyType, r.getUrl(), r.getArg(), r.getHeaders(), r.getRequestMethod());

        Object result = service(bd, new BackendService<ReyResponse>() {
            @Override
            public ReyResponse handle() {
                return clientBackend.handle(r);
            }

            @Override
            public Object fallback(Throwable e) throws Throwable {
                return clientBackend.fallback(FallbacKey.of(proxyMethod), proxyArgs, e);
            }
        });

        if (result == null)
            return null;

        if (result instanceof ReyResponse) {
            ReyResponse reyResponse = (ReyResponse) result;
            if (reyResponse.getStatus() == 204)
                return null;
            try {
                return clientBackend.toObject(r.getReturnType(), r.getGeneType(), reyResponse.getBody());
            } catch (Exception e) {
                throw new MismatchedReturnTypeException("type: " +
                        r.getReturnType() + ", result: " + reyResponse.getBody() + ", url:" + r.getUrl());
            }
        } else if (result.getClass() == r.getReturnType()) {
            return result;
        } else {
            throw new MismatchedReturnTypeException("FALLBACK AND GET MISMATCHED RESULT, " +
                    "catch and invoke e.getTag() to handle", result);
        }
    }

}
