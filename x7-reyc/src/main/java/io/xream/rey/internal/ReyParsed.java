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

import io.xream.x7.base.api.GroupRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sim
 */
public class ReyParsed {

    private Class<?> objectType;
    private String url;
    private Map<String,MethodParsed> map = new HashMap<>();
    private GroupRouter groupRouter;

    public Class<?> getObjectType() {
        return objectType;
    }

    public void setObjectType(Class<?> objectType) {
        this.objectType = objectType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, MethodParsed> getMap() {
        return map;
    }

    public void setMap(Map<String, MethodParsed> map) {
        this.map = map;
    }

    public GroupRouter getGroupRouter() {
        return groupRouter;
    }

    public void setGroupRouter(GroupRouter groupRouter) {
        this.groupRouter = groupRouter;
    }

    @Override
    public String toString() {
        return "ClientParsed{" +
                "objectType=" + objectType +
                ", value='" + url + '\'' +
                ", map=" + map +
                ", groupRouter=" + groupRouter +
                '}';
    }
}
