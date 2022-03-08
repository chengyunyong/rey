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
package io.xream.x7;

import io.xream.x7.rey.DefaultExceptionHandler;
import io.xream.x7.rey.RemoteExceptionHandler;
import io.xream.x7.rey.RestTemplateConfig;
import io.xream.x7.rey.ReyClientConfig;
import io.xream.rey.internal.ClientExceptionHandler;
import io.xream.rey.internal.ClientHeaderInterceptorRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({
        ClientExceptionHandler.class,
        RestTemplateConfig.class,
        ReyClientConfig.class,
        ReyClientBeanRegistrar.class,
        RemoteExceptionHandler.class,
        DefaultExceptionHandler.class,
        ClientHeaderInterceptorRegistrar.class
})
public @interface EnableReyClient {

    String[] basePackages() default {};
    boolean isReyTemplateNotRequired() default false;
}
