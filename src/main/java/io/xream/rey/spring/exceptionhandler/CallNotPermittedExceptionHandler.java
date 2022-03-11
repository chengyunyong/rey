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
package io.xream.rey.spring.exceptionhandler;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.xream.rey.internal.ReyExceptionUtil;
import io.xream.rey.proto.RemoteExceptionProto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;

/**
 * annotation @CircuitBreaker on Controller
 */
@RestControllerAdvice
public class CallNotPermittedExceptionHandler {

    @Resource
    private Tracer tracer;

    @ExceptionHandler({
            CallNotPermittedException.class
    })
    @ResponseBody
    public ResponseEntity<RemoteExceptionProto> handleCallNotPermittedException(CallNotPermittedException exception) {

        Span span = tracer.scopeManager().activeSpan();
        String traceId = span == null ? "" : span.context().toTraceId() + ":" + span.context().toSpanId();
        String stack = "("+ exception.getClass().getName() + ") " + ReyExceptionUtil.getStack(exception);
        RemoteExceptionProto proto = new RemoteExceptionProto(503, exception.getMessage(), stack, traceId);
        return ResponseEntity.status(503).body(
                proto
        );
    }


}
