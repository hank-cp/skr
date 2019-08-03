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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * JSON Serialization for Int value Enum
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class IntValuedEnumSerializer extends StdSerializer<IntValuedEnum> {

    public IntValuedEnumSerializer() {
        this(null);
    }

    public IntValuedEnumSerializer(Class<IntValuedEnum> t) {
        super(t);
    }

    @Override
    public void serialize(IntValuedEnum value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeNumber(value.value());
    }

}
