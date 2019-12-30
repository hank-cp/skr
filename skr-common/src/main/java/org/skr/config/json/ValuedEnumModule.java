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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.stream.Stream;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class ValuedEnumModule extends SimpleModule {

    public ValuedEnumModule() {
        addSerializer(Stream.class, new StreamSerializer());
        addSerializer(ValuedEnum.class, new ValuedEnumSerializer());
        setDeserializers(new ValueEnumDeserializers());
    }

    public class ValueEnumDeserializers extends SimpleDeserializers {

        @Override
        public JsonDeserializer<?> findEnumDeserializer(
                Class<?> type,
                DeserializationConfig config,
                BeanDescription beanDesc) throws JsonMappingException {

            if (!ValuedEnum.class.isAssignableFrom(type)) return null;
            return new ValuedEnumDeserializer((Class<ValuedEnum>) type);
        }
    }
}