package org.skr.config.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Lazy JSON Serialization for Stream
 */
public class StreamSerializer extends StdSerializer<Stream> {

    public StreamSerializer() {
        this(null);
    }

    public StreamSerializer(Class<Stream> t) {
        super(t);
    }

    @Override
    public void serialize(Stream value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        provider.findValueSerializer(Iterator.class, null)
                .serialize(value.iterator(), jgen, provider);
    }

}
