///*
// * Copyright (C) 2019-present the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.skr.config.json;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
//import org.skr.common.util.BeanUtil;
//
//import java.io.IOException;
//import java.lang.reflect.Method;
//
///**
// * JSON Serialization for Int value Enum
// *
// * @author <a href="https://github.com/hank-cp">Hank CP</a>
// */
//public class StringValuedEnumDeserializer extends StdDeserializer<StringValuedEnum> {
//
//    public StringValuedEnumDeserializer() {
//        this(null);
//    }
//
//    public StringValuedEnumDeserializer(Class<StringValuedEnum> t) {
//        super(t);
//    }
//
//    @Override
//    public StringValuedEnum deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
//        Method parseMethod = BeanUtil.getMethod(handledType(), "parse", int.class);
//        String value = jp.getCodec().readValue(jp, String.class);
//        try {
//            return (StringValuedEnum) parseMethod.invoke(parseMethod, value);
//        } catch (Exception e) {
//            throw new IllegalStateException(e.getLocalizedMessage(), e);
//        }
//    }
//}
