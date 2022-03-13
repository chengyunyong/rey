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
package io.xream.rey.spring.beanconfiguration;

import io.xream.rey.api.ClientRestTemplate;
import io.xream.rey.internal.ClientBackend;
import io.xream.rey.internal.DefaultClientRestTemplate;
import io.xream.rey.internal.HttpClientBackendImpl;
import org.springframework.context.annotation.Bean;

/**
 * @author Rolyer Luo
 */
public class ReyClientConfig  {

    @Bean
    public ClientRestTemplate clientTemplate() {
        return new DefaultClientRestTemplate();
    }
    @Bean
    public ClientBackend clientBackend(ClientRestTemplate wrapper)  {
        HttpClientBackendImpl clientBackend = new HttpClientBackendImpl(wrapper);
        return clientBackend;
    }

}
