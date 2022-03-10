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
package io.xream.rey.proto;

import io.xream.internal.util.JsonX;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sim
 */
public class RemoteExceptionProto {

    private int status;
    private String error;
    private String errorOnFallback;
    private List<ExceptionTrace> exceptionTraces = new ArrayList<>();

    public RemoteExceptionProto(){
    }

    public RemoteExceptionProto(int status, String error, String statck, String traceId){
        this.status = status;
        this.error = error;
        ExceptionTrace exceptionTrace = new ExceptionTrace();
        exceptionTrace.setTraceId(traceId);
        exceptionTrace.setStack(statck);
        exceptionTraces.add(exceptionTrace);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public String getErrorOnFallback() {
        return errorOnFallback;
    }

    public void setErrorOnFallback(String errorOnFallback) {
        this.errorOnFallback = errorOnFallback;
    }

    public String toJson(){
        return JsonX.toJson(this);
    }

    public List<ExceptionTrace> getExceptionTraces() {
        return exceptionTraces;
    }

    public void add(ExceptionTrace exceptionTrace) {
        exceptionTraces.add(exceptionTrace);
    }

    public void last(String traceId, String stack) {
        ExceptionTrace exceptionTrace = lastTrace();
        if (exceptionTrace == null){
            exceptionTrace = new ExceptionTrace();
            exceptionTraces.add(exceptionTrace);
        }
        exceptionTrace.setTraceId(traceId);
        exceptionTrace.setStack(stack);
    }

    public ExceptionTrace lastTrace(){
        if (exceptionTraces.isEmpty())
            return null;
        return exceptionTraces.get(exceptionTraces.size() - 1);
    }

    @Override
    public String toString() {
        return toJson();
    }

    public static class ExceptionTrace {
        private String traceId;
        private String stack;
        private String uri;

        public String getTraceId() {
            return traceId;
        }

        public void setTraceId(String traceId) {
            this.traceId = traceId;
        }

        public String getStack() {
            return stack;
        }

        public void setStack(String stack) {
            this.stack = stack;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
