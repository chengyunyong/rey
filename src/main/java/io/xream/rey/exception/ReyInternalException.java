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
package io.xream.rey.exception;

import io.xream.rey.api.ReyHttpStatus;
import io.xream.rey.proto.RemoteExceptionProto;

import java.util.List;

/**
 * @author Sim
 */
public class ReyInternalException extends RuntimeException {

    private int status;
    private String uri;

    private RemoteExceptionProto body;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int status(){
        int s = this.body == null ? this.status : this.body.getStatus();
        return s == 0 ? 500 : s;
    }

    public void add(RemoteExceptionProto.ExceptionTrace exceptionTrace) {
        body.add(exceptionTrace);
    }

    public ReyInternalException() {}

    public ReyInternalException(Throwable e) {
        super(e);
    }


    private ReyInternalException(String message){
        super(message);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public RemoteExceptionProto getBody() {
        return body;
    }

    protected void setBody(RemoteExceptionProto body) {
        this.body = body;
    }

    protected void nextTraceToClient(String path) {
        RemoteExceptionProto.ExceptionTrace next = new RemoteExceptionProto.ExceptionTrace();
        next.setUri(path);
        body.add(next);
    }

    public static ReyInternalException createToClient(String path, RemoteExceptionProto proto) {
        ReyInternalException.ToClient exception = new ReyInternalException.ToClient();
        exception.setBody(proto);
        exception.nextTraceToClient(path);
        return exception;
    }

    public static ReyInternalException createToClient(int status, String error, String uri, List<RemoteExceptionProto.ExceptionTrace> traces) {

        if (status == ReyHttpStatus.BAD_REQUEST.getStatus()) {
            return new ReyInternalException.BadRequest(error,uri,traces);
        }

        return new ReyInternalException.ToClient(status,error,uri,traces);
    }

    public static ReyInternalException create(int status, String error,
                                              String stack,
                                              String fallback,
                                              String path){

        if (status == ReyHttpStatus.BAD_REQUEST.getStatus()){
            return new BadRequest(error,stack,fallback,path);
        }else {
            return new ToClient(status,error,stack,fallback,path);
        }
    }

    public void setErrorOnFallback(String message) {
        if (body == null) {
            this.body = new RemoteExceptionProto();
        }
        this.body.setErrorOnFallback(message);
    }

    public String uriOfLast() {
        RemoteExceptionProto.ExceptionTrace exceptionTrace = this.body.lastTrace();
        if (exceptionTrace == null)
            return "";
        return exceptionTrace.getUri();
    }

    public static final class BadRequest extends ReyInternalException {

        private BadRequest(String error,String stack,String fallback,String path) {
            super();
            super.setStatus(400);
            RemoteExceptionProto.ExceptionTrace exceptionTrace = new RemoteExceptionProto.ExceptionTrace();
            exceptionTrace.setStack(stack);
            exceptionTrace.setUri(path);

            RemoteExceptionProto proto = new RemoteExceptionProto();
            proto.setStatus(400);
            proto.setError(error);
            proto.setErrorOnFallback(fallback);
            proto.add(exceptionTrace);

            super.setBody(proto);
        }

        public BadRequest(String error, String path, List<RemoteExceptionProto.ExceptionTrace> traces) {
            super();
            super.setStatus(400);
            RemoteExceptionProto.ExceptionTrace exceptionTrace = new RemoteExceptionProto.ExceptionTrace();
            exceptionTrace.setUri(path);

            RemoteExceptionProto proto = new RemoteExceptionProto();
            proto.setStatus(400);
            proto.setError(error);
            proto.getExceptionTraces().addAll(traces);
            proto.add(exceptionTrace);

            super.setBody(proto);
        }

    }

    public static final class ToClient extends ReyInternalException {

        private ToClient() {}

        private ToClient(int status, String error,String stack, String fallback,String path) {
            super();
            super.setStatus(status);
            RemoteExceptionProto.ExceptionTrace exceptionTrace = new RemoteExceptionProto.ExceptionTrace();
            exceptionTrace.setStack(stack);
            exceptionTrace.setUri(path);

            RemoteExceptionProto proto = new RemoteExceptionProto();
            proto.setStatus(status);
            proto.setError(error);
            proto.setErrorOnFallback(fallback);
            proto.add(exceptionTrace);

            super.setBody(proto);
        }

        public ToClient(int status, String error, String path, List<RemoteExceptionProto.ExceptionTrace> traces) {
            super();
            super.setStatus(status);
            RemoteExceptionProto.ExceptionTrace exceptionTrace = new RemoteExceptionProto.ExceptionTrace();
            exceptionTrace.setUri(path);

            RemoteExceptionProto proto = new RemoteExceptionProto();
            proto.setStatus(status);
            proto.setError(error);
            proto.getExceptionTraces().addAll(traces);
            proto.add(exceptionTrace);

            super.setBody(proto);
        }

    }
}
