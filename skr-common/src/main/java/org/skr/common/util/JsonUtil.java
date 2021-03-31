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
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.laxture.spring.util.ApplicationContextProvider;
import org.skr.config.json.ExtendableLocalDateTimeDeserializer;
import org.skr.config.json.JsonSkipPersistence;
import org.skr.config.json.ValuedEnumModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class JsonUtil {

    public static final String JSON_FILTER_SKIP_PERSISTENCE = "skipPersistence";

    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = ApplicationContextProvider.getBean(ObjectMapper.class);
        if (objectMapper == null) {
            objectMapper = Jackson2ObjectMapperBuilder.json().build();
            setupObjectMapper(objectMapper);
        }
        return objectMapper;
    }

    public static ObjectMapper setupObjectMapper(ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new ValuedEnumModule());
        // override JavaTimeModule to support more date time format
        objectMapper.configure(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS, false);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, ExtendableLocalDateTimeDeserializer.INSTANCE);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern(ExtendableLocalDateTimeDeserializer.FORMAT_DEFAULT)));
        objectMapper.registerModule(javaTimeModule);

        objectMapper.setVisibility(objectMapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY))
                .setFilterProvider(new SimpleFilterProvider()
                    .addFilter(JSON_FILTER_SKIP_PERSISTENCE, SimpleBeanPropertyFilter.serializeAll()));
        return objectMapper;
    }

    public static ObjectMapper setupPersistentObjectMapper(ObjectMapper objectMapper) {
        PropertyFilter ignoreFilter = new SimpleBeanPropertyFilter() {
            @Override
            protected boolean include(BeanPropertyWriter writer) {
                return writer.getAnnotation(JsonSkipPersistence.class) == null;
            }

            @Override
            protected boolean include(PropertyWriter writer) {
                return writer.getAnnotation(JsonSkipPersistence.class) == null;
            }
        };
        // REQUIRE_SETTERS_FOR_GETTERS causes ignoring getter
        return objectMapper.copy().configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true)
                .setFilterProvider(new SimpleFilterProvider()
                .addFilter(JSON_FILTER_SKIP_PERSISTENCE, ignoreFilter));
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
        if (json == null) return null;
        try {
            return (T) objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException("Deserialize json text failed. "+json, e);
        }
    }

    public static <T> T fromJson(ObjectMapper objectMapper,
                                 final TypeReference<T> type,
                                 final String json) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException("Deserialize json text failed. "+json, e);
        }
    }

    public static <T> T fromJson(ObjectMapper objectMapper,
                                 JavaType javaType,
                                 String json) {
        if (json == null) return null;
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
        if (jsonNode == null) return null;
        return (T) objectMapper.convertValue(jsonNode, type);
    }

    public static <T> T fromJson(ObjectMapper objectMapper,
                                 final TypeReference<T> type,
                                 final JsonNode jsonNode) {
        if (jsonNode == null) return null;
        return objectMapper.convertValue(jsonNode, type);
    }

    public static <T> T fromJson(ObjectMapper objectMapper,
                                 JavaType javaType,
                                 JsonNode jsonNode) {
        if (jsonNode == null) return null;
        return objectMapper.convertValue(jsonNode, javaType);
    }

    public static String toJson(ObjectMapper objectMapper,
                                Object obj, Class<?> jsonViewClazz) {
        if (obj == null) return null;
        try {
            return objectMapper.writerWithView(jsonViewClazz).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialize object<"+obj.getClass().getName()+"> to json failed.", e);
        }
    }

    public static String toJson(ObjectMapper objectMapper, Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialize object<"+obj.getClass().getName()+"> to json failed.", e);
        }
    }

    public static JsonNode toJsonNode(ObjectMapper objectMapper, Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.valueToTree(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialize object<"+obj.getClass().getName()+"> to json failed.", e);
        }
    }

    public static JavaType getJavaType(Class<?> clazz) {
        return getObjectMapper().getSerializationConfig().getTypeFactory().constructType(clazz);
    }

    public static JavaType getCollectionJavaType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return getObjectMapper().getSerializationConfig().getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

}
