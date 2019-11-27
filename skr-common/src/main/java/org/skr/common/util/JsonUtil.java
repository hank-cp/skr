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
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.laxture.spring.util.ApplicationContextProvider;
import org.skr.config.json.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class JsonUtil {

    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = ApplicationContextProvider.getBean(ObjectMapper.class);
        if (objectMapper == null) {
            objectMapper = newObjectMapperBuilder().build();
        }
        return objectMapper;
    }

    public static Jackson2ObjectMapperBuilder newObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .modulesToInstall(getDefaultModule(), new Jdk8Module(), new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .visibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .visibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .visibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
    }

    public static SimpleModule getDefaultModule() {
        return new SimpleModule()
                .addSerializer(IntValuedEnum.class, new IntValuedEnumSerializer())
                .addSerializer(StringValuedEnum.class, new StringValuedEnumSerializer())
                .addSerializer(Stream.class, new StreamSerializer());
    }

    //*************************************************************************
    // Using Shared ObjectMapper
    //*************************************************************************

    public static <T> T fromJson(final Class<?> type,
                                 final String json) {
        return fromJson(getObjectMapper(), type, json);
    }

    public static <T> T fromJson(final TypeReference<T> type,
                                 final String json) {
        return fromJson(getObjectMapper(), type, json);
    }

    public static <T> T fromJson(final JavaType javaType,
                                 final String json) {
        return fromJson(getObjectMapper(), javaType, json);
    }

    public static <T> T fromJson(final Class<?> type,
                                 final JsonNode jsonNode) {
        return fromJson(getObjectMapper(), type, jsonNode);
    }

    public static <T> T fromJson(final TypeReference<T> type,
                                 final JsonNode jsonNode) {
        return fromJson(getObjectMapper(), type, jsonNode);
    }

    public static <T> T fromJson(final JavaType javaType,
                                 final JsonNode jsonNode) {
        return fromJson(getObjectMapper(), javaType, jsonNode);
    }

    public static String toJson(Object obj, Class<?> jsonViewClazz) {
        return toJson(getObjectMapper(), obj, jsonViewClazz);
    }

    public static String toJson(Object obj) {
        return toJson(getObjectMapper(), obj);
    }

    public static JsonNode toJsonNode(Object obj) {
        return toJsonNode(getObjectMapper(), obj);
    }

    //*************************************************************************
    // Using Instanced ObjectMapper
    //*************************************************************************

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(ObjectMapper objectMapper,
                                 final Class<?> type,
                                 final String json) {
        try {
            return (T) objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException("Deserialize json text failed. "+json, e);
        }
    }

    public static <T> T fromJson(ObjectMapper objectMapper,
                                 final TypeReference<T> type,
                                 final String json) {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException("Deserialize json text failed. "+json, e);
        }
    }

    public static <T> T fromJson(ObjectMapper objectMapper,
                                 JavaType javaType,
                                 String json) {
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException("Deserialize json text failed. "+json, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(ObjectMapper objectMapper,
                                 final Class<?> type,
                                 final JsonNode jsonNode) {
        return (T) objectMapper.convertValue(jsonNode, type);
    }

    public static <T> T fromJson(ObjectMapper objectMapper,
                                 final TypeReference<T> type,
                                 final JsonNode jsonNode) {
        return objectMapper.convertValue(jsonNode, type);
    }

    public static <T> T fromJson(ObjectMapper objectMapper,
                                 JavaType javaType,
                                 JsonNode jsonNode) {
        return objectMapper.convertValue(jsonNode, javaType);
    }

    public static String toJson(ObjectMapper objectMapper,
                                Object obj, Class<?> jsonViewClazz) {
        try {
            return objectMapper.writerWithView(jsonViewClazz).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialize object<"+obj.getClass().getName()+"> to json failed.", e);
        }
    }

    public static String toJson(ObjectMapper objectMapper, Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialize object<"+obj.getClass().getName()+"> to json failed.", e);
        }
    }

    public static JsonNode toJsonNode(ObjectMapper objectMapper, Object obj) {
        try {
            return objectMapper.valueToTree(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialize object<"+obj.getClass().getName()+"> to json failed.", e);
        }
    }

    public static JavaType getJavaType(Class<?> clazz) {
        return JsonUtil.getObjectMapper().getSerializationConfig().getTypeFactory().constructType(clazz);
    }

    public static JavaType getCollectionJavaType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return JsonUtil.getObjectMapper().getSerializationConfig().getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

}
