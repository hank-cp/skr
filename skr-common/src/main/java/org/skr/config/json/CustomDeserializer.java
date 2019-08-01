package org.skr.config.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.skr.config.ApplicationContextProvider;
import org.skr.registry.Registry;

import java.io.IOException;

/**
 *
 */
@SuppressWarnings("unchecked")
public class CustomDeserializer<T> extends StdDeserializer<T> {

    public CustomDeserializer(Class<T> t) {
        super(t);
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        ObjectMapper objectMapper = ApplicationContextProvider.getBean(ObjectMapper.class);
        return (T) objectMapper.treeToValue(node, handledType());
    }
}
