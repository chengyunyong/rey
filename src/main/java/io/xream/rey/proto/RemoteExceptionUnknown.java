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

import java.util.List;

/**
 * @author Sim
 */
public class RemoteExceptionUnknown {

    private int status;
    private String error;
    private String path;
    private List<RemoteExceptionProto.ExceptionTrace> exceptionTraces;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<RemoteExceptionProto.ExceptionTrace> getExceptionTraces() {
        return exceptionTraces;
    }

    public void setExceptionTraces(List<RemoteExceptionProto.ExceptionTrace> exceptionTraces) {
        this.exceptionTraces = exceptionTraces;
    }
}
