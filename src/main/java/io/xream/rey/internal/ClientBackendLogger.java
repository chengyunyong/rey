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
import io.xream.internal.util.LoggerProxy;
import io.xream.internal.util.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.stream.Collectors;

/**
 * @author Sim
 */
public class ClientBackendLogger {

    public static void log(Class clz, String url, Object arg, HttpHeaders headers, RequestMethod requestMethod) {

        StringBuilder headerStr = new StringBuilder();

        if (headers.getContentType() == null
            && (
                    requestMethod == RequestMethod.POST
                || requestMethod == RequestMethod.PUT
                || requestMethod == RequestMethod.PATCH
                )
        ) {
            headerStr.append(" -H Content-Type:application/json");
        }

        headers.entrySet().stream().forEach(
                header -> headerStr.append(" -H ").append(header.getKey()).append(":").append(header.getValue().stream().collect(Collectors.joining()))
        );
        String json = arg == null ? "" : JsonX.toJson(arg);

        LoggerProxy.debug(clz, "-X " + requestMethod.name() + "  " + url + headerStr + (StringUtil.isNotNull(json) ? (" -d '" + json + "'") : ""));
    }
}
