package org.skr.config;

import org.skr.common.util.Checker;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.List;

public class YamlPropertyLoaderFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        // after Spring Boot 2.0
        List<PropertySource<?>> sources = new YamlPropertySourceLoader().
                load(resource.getResource().getFilename(), resource.getResource());
        if (Checker.isEmpty(sources)) return null;
        return sources.get(0);

        // before Spring Boot 2.0
//        return new YamlPropertySourceLoader().
//                load(resource.getResource().getFilename(), resource.getResource(), null);
    }
}
