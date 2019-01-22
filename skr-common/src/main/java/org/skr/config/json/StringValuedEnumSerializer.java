package org.skr.config.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * 带值Enum的JSON序列化
 */
public class StringValuedEnumSerializer extends StdSerializer<StringValuedEnum> {

    public StringValuedEnumSerializer() {
        this(null);
    }

    public StringValuedEnumSerializer(Class<StringValuedEnum> t) {
        super(t);
    }

    @Override
    public void serialize(StringValuedEnum value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeString(value.value());
    }

}
