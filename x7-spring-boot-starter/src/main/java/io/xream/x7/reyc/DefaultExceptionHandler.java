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
package io.xream.x7.reyc;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.xream.x7.base.exception.MismatchedReturnTypeException;
import io.xream.x7.base.exception.ReyBizException;
import io.xream.x7.base.util.ExceptionUtil;
import io.xream.x7.base.web.RemoteExceptionProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.annotation.Resource;

@RestControllerAdvice
public class DefaultExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);
    @Resource
    private Tracer tracer;

    @ExceptionHandler({
            RuntimeException.class
    })
    @ResponseBody
    public ResponseEntity<RemoteExceptionProto> handleDefaultException(RuntimeException e){

        logger.error(ExceptionUtil.getMessage(e));

        if (e.getClass().getName().startsWith("org.springframework.http"))
            throw e;

        Span span = tracer.scopeManager().activeSpan();
        String traceId = span == null ? "" : span.context().toTraceId()+ ":" + span.context().toSpanId();

        String stack = ExceptionUtil.getStack(e);
        int status = 500;
        String message = null;
        if (e instanceof IllegalArgumentException
                || e instanceof MethodArgumentTypeMismatchException
                || e instanceof MethodArgumentConversionNotSupportedException
        ) {
            status = 400;
            message = e.getMessage();
        }else if (e instanceof MismatchedReturnTypeException){
            message = "("+MismatchedReturnTypeException.class.getName() + ") " + e.getMessage();
        }else if (e instanceof ReyBizException){
            message = "("+ReyBizException.class.getName() + ") " + e.getMessage();
        } else {
            message = e.getMessage();
        }
        RemoteExceptionProto proto = new RemoteExceptionProto(status,message,stack,traceId);
        return ResponseEntity.status(status == 400 ? 400 : 500).body(
                proto
        );
    }


}
