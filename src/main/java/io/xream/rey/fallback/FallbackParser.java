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
package io.xream.rey.fallback;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sim
 */
public class FallbackParser {

    private static final Map<FallbacKey, FallbackParsed> map = new HashMap<>();

    public static Collection<FallbackParsed> all(){
        return map.values();
    }

    public static FallbackParsed get(FallbacKey key) {
        return map.get(key);
    }


    public static void init(Class<? extends Throwable>[] ignoreExceptions,  Class<?> serviceClz, Class<?> fallbackClz){
        {

            if (fallbackClz != null && fallbackClz != void.class) {

                Method[] arr = serviceClz.getDeclaredMethods();
                Method[] fallbackMethodArr = fallbackClz.getMethods();
                for (Method fm : fallbackMethodArr) {
                    for (Method reyMethod : arr) {
                        if (reyMethod.getName().equals(fm.getName())) {
                            map.put(FallbacKey.of(reyMethod),
                                    FallbackParsed.of(fm,null,ignoreExceptions,serviceClz)
                            );
                        }
                    }
                }
            }
        }
    }

    public static void parse(FallbackInstance fallbackInstance) {

        for (Map.Entry<FallbacKey, FallbackParsed> entry: map.entrySet()) {

            FallbackParsed parsed = entry.getValue();
            Class clzz = parsed.getMethod().getDeclaringClass();
            Object fallback = fallbackInstance.get(clzz);
            parsed.setFallback(fallback);
        }
    }

    public interface FallbackInstance {
        Object get(Class<?> fallback);
    }
}
