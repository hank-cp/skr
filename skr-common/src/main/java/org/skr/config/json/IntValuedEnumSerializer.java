package org.skr.config.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * JSON Serialization for Int value Enum
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
