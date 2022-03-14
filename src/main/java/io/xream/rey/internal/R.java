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

import io.xream.internal.util.StringUtil;
import io.xream.rey.api.GroupRouter;
import io.xream.rey.api.UrlParamed;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author Sim
 */
public class R {
    private String url;
    private Class<?> returnType;
    private Class<?> geneType;
    private Object arg;
    private RequestMethod requestMethod;
    private HttpHeaders headers;
    private GroupRouter router;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Class<?> getGeneType() {
        return geneType;
    }

    public void setGeneType(Class<?> geneType) {
        this.geneType = geneType;
    }

    public Object getArg() {
        return arg;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public GroupRouter getRouter() {
        return router;
    }

    public void setRouter(GroupRouter router) {
        this.router = router;
    }

    public static R build(Class proxyType, Method proxyMethod, Object[] args) {

        String clzzName = proxyType.getName();
        String methodName = proxyMethod.getName();

        ReyParsed parsed = ReyParser.get(clzzName);
        String url = parsed.getUrl();

        MethodParsed methodParsed = parsed.getMap().get(methodName);

        Objects.requireNonNull(methodParsed);

        url = url + methodParsed.getRequestMapping();

        List<Object> objectList = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(methodParsed.getHeaders());
        if (args != null) {
            for (Object arg : args) {
                if (arg != null && arg instanceof UrlParamed) {
                    UrlParamed urlParamed = (UrlParamed) arg;
                    url = urlParamed.value();
                } else if (arg != null && arg instanceof MultiValueMap) {
                    headers.addAll((MultiValueMap) arg);
                } else {
                    objectList.add(arg);
                }
            }
        }

        url = resolveUrl(url, parsed.getGroupRouter(), objectList);
        args = objectList.toArray();

        RequestMethod requestMethod = methodParsed.getRequestMethod();

        R r = new R();
        r.setArg( (args != null && args.length > 0) ? args[0] : null);
        r.setRequestMethod(requestMethod);
        r.setReturnType(methodParsed.getReturnType());
        r.setGeneType(methodParsed.getGeneType());
        r.setUrl(url);
        r.setHeaders(headers);
        r.setRouter(parsed.getGroupRouter());
        return r;
    }

    private static Pattern pattern = Pattern.compile("\\{[\\w]*\\}");

    private static String resolveUrl(String url, GroupRouter router, List<Object> argList) {

        if (!url.startsWith("http")) {
            url = "http://" + url;
        }

        if (router != null) {
            Object arg = null;
            if (argList != null && argList.size() > 0) {
                arg = argList.get(0);
                url = url.replace(router.replaceHolder(), router.replaceValue(arg));
            }
        }

        if (url.contains("{")) {
            List<String> regExList = StringUtil.listByRegEx(url, pattern);
            int size = regExList.size();
            for (int i = 0; i < size; i++) {
                Object arg = argList.remove(0);
                url = url.replace(regExList.get(i), arg.toString());
            }
        }

        return url;
    }

    @Override
    public String toString() {
        return "R{" +
                "url='" + url + '\'' +
                ", returnType=" + returnType +
                ", geneType=" + geneType +
                ", args=" + arg +
                ", requestMethod=" + requestMethod +
                ", headers=" + headers +
                '}';
    }

}
