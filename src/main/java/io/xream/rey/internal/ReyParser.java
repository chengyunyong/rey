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

import io.xream.rey.annotation.ReyClient;
import io.xream.rey.api.GroupRouter;
import io.xream.rey.fallback.FallbackParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sim
 */
public class ReyParser {

    private static Logger logger = LoggerFactory.getLogger(ReyParser.class);

    private final static Map<String, ReyParsed> map = new HashMap<>();

    public static ReyParsed get(String intfName) {

        return map.get(intfName);
    }

    public static void init(Class<?> clz, UrlConfigurable urlConfigurable) {
        Annotation reyClientAnno = clz.getAnnotation(ReyClient.class);
        if (reyClientAnno == null)
            return;

        ReyClient reyClient = (ReyClient) reyClientAnno;

        String url = urlConfigurable.config(reyClient.value());

        ReyParsed parsed = new ReyParsed();
        parsed.setObjectType(clz);
        parsed.setUrl(url);

        String clzName = clz.getName();
        if (map.containsKey(clzName)) {
            logger.error("Parsing {}, found repeated class: {}", "ReyClient", clzName);
        }
        map.put(clzName, parsed);

        parse();
    }

    public static void parse() {


        for (Map.Entry<String,ReyParsed> entry : map.entrySet()) {

            ReyParsed parsed = entry.getValue();
            Class<?> clz = parsed.getObjectType();
            ReyClient reyClient = clz.getAnnotation(ReyClient.class);

            FallbackParser.init(reyClient.ignoreExceptions(), clz, reyClient.fallback());

            /*
             * groupRouter
             */
            Class<? extends GroupRouter> groupRouterClz = reyClient.groupRouter();
            if (groupRouterClz != GroupRouter.class) {
                try {
                    parsed.setGroupRouter(groupRouterClz.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Method[] arr = clz.getDeclaredMethods();
            for (Method method : arr) {

                String methodName = method.getName();
                Class<?> returnType = method.getReturnType();

                Annotation mappingAnno = method.getAnnotation(RequestMapping.class);
                if (mappingAnno == null) {
                    logger.error(clz.getName() + "." + methodName + ", Not Found Annotation: " + RequestMapping.class.getName());
                    System.exit(0);
                }

                RequestMapping requestMapping = (RequestMapping) mappingAnno;
                if (requestMapping.value() == null || requestMapping.value().length == 0) {
                    logger.error(clz.getName() + "." + methodName + " RequestMapping, no mapping value");
                    System.exit(0);
                }

                String mapping = requestMapping.value()[0];

                RequestMethod rm = null;
                RequestMethod[] rmArr = requestMapping.method();
                if (rmArr == null || rmArr.length == 0) {
                    if (mapping != null && mapping.contains("{") && mapping.contains("}")) {
                        rm = RequestMethod.GET;
                    } else {
                        rm = RequestMethod.POST;
                    }

                } else {
                    rm = rmArr[0];
                }

                MultiValueMap map = new LinkedMultiValueMap();
                String[] headers = requestMapping.headers();
                if (headers != null && headers.length > 0) {
                    for (String header : headers) {
                        header = header.replace(":", "=");
                        int i = header.indexOf("=");
                        String key = header.substring(0, i);
                        String value = header.substring(i + 1);
                        map.add(key.trim(), value.trim());
                    }
                }

                if (returnType == Map.class) {
                    logger.info("ReyClient not support  genericReturnType of Map，while parsing " + method);
                    logger.error("ReyClient not support  genericReturnType of Map，while parsing " + method);
                    System.exit(0);
                }

                Class gtc = null;
                if (returnType == List.class) {
                    Type gt = method.getGenericReturnType();
                    ParameterizedType pt = (ParameterizedType) gt;
                    Type t = pt.getActualTypeArguments()[0];
                    if (t instanceof ParameterizedType) {
                        logger.error("ReyClient not support complex genericReturnType, like List<List<?>>, or" +
                                "List<Map>，while parsing " + method);
                        System.exit(0);
                    }
                    gtc = (Class) t;
                }

                MethodParsed methodParsed = new MethodParsed();
                methodParsed.setRequestMapping(mapping);
                methodParsed.setReturnType(returnType);
                methodParsed.setGeneType(gtc);
                methodParsed.setRequestMethod(rm);
                methodParsed.setHeaders(map);

                parsed.getMap().put(methodName, methodParsed);
            }
        }

    }


    public interface UrlConfigurable {
        String config(String pattern);
    }
}
