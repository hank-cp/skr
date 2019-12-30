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
package org.skr.config.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.skr.common.util.BeanUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * JSON Serialization for Int value Enum
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class ValuedEnumDeserializer extends StdDeserializer<ValuedEnum> {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPES = Map.of(
            Boolean.class,      boolean.class,
            Byte.class,         byte.class,
            Character.class,    char.class,
            Double.class,       double.class,
            Float.class,        float.class,
            Integer.class,      int.class,
            Long.class,         long.class,
            Short.class,        short.class,
            Void.class,         void.class
    );

    public ValuedEnumDeserializer() {
        this(null);
    }

    public ValuedEnumDeserializer(Class<ValuedEnum> t) {
        super(t);
    }

    @Override
    public ValuedEnum deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Optional<Class> valueType = Arrays.stream(handledType().getGenericInterfaces())
                .filter(it -> it instanceof ParameterizedType)
                .map(it -> (Class) ((ParameterizedType) it).getActualTypeArguments()[0])
                .findAny();

        if (valueType.isEmpty()) {
            throw new IllegalStateException(handledType()+" is not valid ValuedEnum.");
        }

        Method parseMethod = BeanUtil.getMethod(handledType(), "parse", valueType.get());
        if (parseMethod == null) {
            // try to get premitive version "parse" method
            parseMethod = BeanUtil.getMethod(handledType(), "parse", PRIMITIVE_TYPES.get(valueType.get()));
        }
        Object value = jp.getCodec().readValue(jp, valueType.get());
        try {
            return (ValuedEnum) parseMethod.invoke(parseMethod, value);
        } catch (Exception e) {
            throw new IllegalStateException(e.getLocalizedMessage(), e);
        }
    }
}
