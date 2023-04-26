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
package demo.skr.reg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import demo.skr.reg.model.Permission;
import org.skr.config.json.CustomDeserializer;
import org.skr.permission.IPermission;
import org.skr.security.PermissionDetail;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Configuration
public class PermConfiguration {

    @Configuration
    @AutoConfigureAfter(JacksonAutoConfiguration.class)
    public static class JacksonConfigurer implements InitializingBean {

        @Autowired
        private ObjectMapper objectMapper;

        @SuppressWarnings("Duplicates")
        @Override
        public void afterPropertiesSet() {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(PermissionDetail.class, new CustomDeserializer<>(Permission.class));
            module.addDeserializer(IPermission.class, new CustomDeserializer<>(Permission.class));
            objectMapper.registerModule(module);
        }
    }
}