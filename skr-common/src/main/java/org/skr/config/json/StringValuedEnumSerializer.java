package org.skr.config.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * JSON Serialization for String value Enum
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
