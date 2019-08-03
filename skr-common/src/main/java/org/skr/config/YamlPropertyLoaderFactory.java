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
