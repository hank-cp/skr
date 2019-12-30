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
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * JSON Serialization for Int value Enum
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class ValuedEnumSerializer extends StdSerializer<ValuedEnum>{

    public ValuedEnumSerializer() {
        this(null);
    }

    public ValuedEnumSerializer(Class<ValuedEnum> t) {
        super(t);
    }

    @Override
    public void serialize(ValuedEnum value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException {
        Object v = value.value();
        if (short.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((short) v);

        } else if (Short.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((Short) v);

        } else if (int.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((int) v);

        } else if (Integer.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((Integer) v);

        } else if (long.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((long) v);

        } else if (Long.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((Long) v);

        } else if (BigInteger.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((BigInteger) v);

        } else if (double.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((double) v);

        } else if (Double.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((Double) v);

        } else if (float.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((float) v);

        } else if (Float.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((Float) v);

        } else if (BigDecimal.class.isAssignableFrom(v.getClass())) {
            jgen.writeNumber((BigDecimal) v);

        } else {
            jgen.writeString(v.toString());
        }
    }

}
