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

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;

/**
 * JSON Serialization for Int value Enum
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class ValuedEnumDeserializer extends StdDeserializer<ValuedEnum> {

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

        Object value = jp.getCodec().readValue(jp, valueType.get());
        return ValuedEnum.parse(handledType(), value);
    }
}
