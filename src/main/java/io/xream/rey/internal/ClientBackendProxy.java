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

import io.xream.internal.util.LoggerProxy;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @author Sim
 */
public class ClientBackendProxy implements FactoryBean {

    private Class<?> objectType;

    private BackendDecoration backendDecoration;
    private ClientBackend clientBackend;

    private String service;
    private String url;
    private Class<? extends Throwable>[] ignoreExceptions;
    private Class fallback;
    private String config;

    public void setService(String service) {
        this.service = service;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIgnoreExceptions(Class<? extends Throwable>[] ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    public void setFallback(Class fallback) {
        this.fallback = fallback;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(objectType.getClassLoader(), new Class[]{objectType},new ClientBackendInvocationHandler(this));
    }


    public void setObjectType(Class<?> objectType){
        this.objectType = objectType;
        LoggerProxy.put(objectType, LoggerFactory.getLogger(objectType));
    }

    @Override
    public Class<?> getObjectType() {
        return this.objectType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public BackendDecoration getBackendDecoration() {
        return backendDecoration;
    }

    public void setBackendDecoration(BackendDecoration backendDecoration) {
        this.backendDecoration = backendDecoration;
    }

    public ClientBackend getClientBackend() {
        return clientBackend;
    }

    public void setClientBackend(ClientBackend clientBackend) {
        this.clientBackend = clientBackend;
    }

    @Override
    public String toString() {
        return  service +"{" +
                "url=" + url +
                ", fallback=" + fallback.getSimpleName() +
                ", config=" + config  +
                ", ignoreExceptions=" + exceptionsString() +
                '}';
    }

    private String exceptionsString(){
        if (this.ignoreExceptions == null)
            return "[]";
        int length = this.ignoreExceptions.length;
        if (length == 0)
            return "[]";
        String[] arr = new String[length];
        for (int i=0; i<length; i++) {
            arr[i] = this.ignoreExceptions[i].getSimpleName();
        }
        return Arrays.toString(arr);
    }
}
