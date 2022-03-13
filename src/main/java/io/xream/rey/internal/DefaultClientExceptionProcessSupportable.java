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

import io.xream.rey.api.CallNotPermittedExceptionConverter;
import io.xream.rey.api.ClientExceptionProcessSupportable;
import io.xream.rey.api.FallbackDeterminate;
import io.xream.rey.api.RespondedExceptionConverter;

/**
 * @author Sim
 */
public class DefaultClientExceptionProcessSupportable implements ClientExceptionProcessSupportable {

    private RespondedExceptionConverter respondedExceptionConverter;
    private CallNotPermittedExceptionConverter callNotPermittedExceptionConverter;
    private FallbackDeterminate fallbackDeterminate;

    public void setRespondedExceptionConverter(RespondedExceptionConverter respondedExceptionConverter) {
        this.respondedExceptionConverter = respondedExceptionConverter;
    }

    public void setCallNotPermittedExceptionConverter(CallNotPermittedExceptionConverter handler) {
        this.callNotPermittedExceptionConverter = handler;
    }

    public void setFallbackDeterminate(FallbackDeterminate fallbackDeterminate) {
        this.fallbackDeterminate = fallbackDeterminate;
    }


    @Override
    public FallbackDeterminate fallbackDeterminate() {
        return this.fallbackDeterminate;
    }

    @Override
    public RespondedExceptionConverter respondedExceptionConverter() {
        return this.respondedExceptionConverter;
    }

    @Override
    public CallNotPermittedExceptionConverter callNotPermittedExceptionConverter() {
        return this.callNotPermittedExceptionConverter;
    }

}
