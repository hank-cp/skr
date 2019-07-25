package demo.skr;/*
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import demo.skr.model.registry.EndPoint;
import demo.skr.model.registry.Permission;
import demo.skr.model.registry.Realm;
import org.skr.config.YamlPropertyLoaderFactory;
import org.skr.registry.EndPointRegistry;
import org.skr.registry.PermissionRegistry;
import org.skr.registry.RealmRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Configuration
@EnableCaching
@PropertySource(value = "classpath:common.yml",
        factory = YamlPropertyLoaderFactory.class)
public class CommonConfiguration {

    @Configuration
    @AutoConfigureAfter(JacksonAutoConfiguration.class)
    @ConditionalOnProperty(value = "demo.skr.registry-manager", matchIfMissing = true, havingValue = "false")
    public static class OnStartUpListener implements InitializingBean {

        @Autowired
        private ObjectMapper objectMapper;

        @SuppressWarnings("Duplicates")
        @Override
        public void afterPropertiesSet() {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(RealmRegistry.class, new RegistryDeserializer<>(Realm.class));
            module.addDeserializer(PermissionRegistry.class, new RegistryDeserializer<>(Permission.class));
            module.addDeserializer(EndPointRegistry.class, new RegistryDeserializer<>(EndPoint.class));
            objectMapper.registerModule(module);
        }
    }
}