package org.skr.registry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.skr.common.Constants;
import org.skr.common.exception.ConfException;
import org.skr.common.exception.Errors;
import org.skr.common.util.Checker;
import org.skr.config.ApplicationContextProvider;
import org.skr.registry.model.AppSvr;
import org.skr.registry.model.Permission;
import org.skr.registry.model.Registry;
import org.skr.registry.model.SiteUrl;

import java.io.IOException;
import java.util.Optional;

/**
 * 带值Enum的JSON序列化
 */
public class RegistryDeserializer<T extends Registry> extends StdDeserializer<T> {

    public RegistryDeserializer() {
        this(null);
    }

    public RegistryDeserializer(Class<T> t) {
        super(t);
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String type = Optional.ofNullable(node.get("type")).map(JsonNode::asText).orElse(null);
        if (Checker.isEmpty(type)) {
            throw new ConfException(Errors.REGISTRATION_ERROR.setMsg("Registry type field is not set."));
        }
        ObjectMapper objectMapper = ApplicationContextProvider.getBean(ObjectMapper.class);
        switch (type) {
            case Constants.REGISTRY_APPSVR:
                return (T) objectMapper.treeToValue(node, AppSvr.class);
            case Constants.REGISTRY_PERMISSION:
                return (T) objectMapper.treeToValue(node, Permission.class);
            case Constants.REGISTRY_SITEURL:
                return (T) objectMapper.treeToValue(node, SiteUrl.class);
            default:
                throw new ConfException(Errors.REGISTRATION_ERROR.setMsg("Unsupported registry type", type));
        }
    }
}
