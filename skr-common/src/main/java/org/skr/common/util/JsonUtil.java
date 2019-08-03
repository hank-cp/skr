/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skr.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.skr.config.ApplicationContextProvider;
import org.skr.config.json.*;

import java.util.stream.Stream;

public class JsonUtil {

    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = ApplicationContextProvider.getBean(ObjectMapper.class);
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            setupObjectMapper(objectMapper);
        }
        return objectMapper;
    }

    /** Additional Config for Jackson ObjectMapper */
    public static void setupObjectMapper(ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule();
        module.addSerializer(IntValuedEnum.class, new IntValuedEnumSerializer());
        module.addSerializer(StringValuedEnum.class, new StringValuedEnumSerializer());
        module.addSerializer(Stream.class, new StreamSerializer());
        objectMapper.registerModule(module);

        objectMapper.setVisibility(objectMapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY));
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJSON(final Class<?> type,
                                 final String json) {
        try {
            return (T) getObjectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Deserialize json text failed. "+json, e);
        }
    }

    public static <T> T fromJSON(final TypeReference<T> type,
                                 final String json) {
        try {
            return getObjectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Deserialize json text failed. "+json, e);
        }
    }

    public static String toJSON(Object obj, Class<?> jsonViewClazz) {
        try {
            return getObjectMapper().writerWithView(jsonViewClazz).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialize object<"+obj.getClass().getName()+"> to json failed.", e);
        }
    }

    public static String toJSON(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialize object<"+obj.getClass().getName()+"> to json failed.", e);
        }
    }

}
