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
package demo.skr.aio;

import demo.skr.registry.model.EndPoint;
import demo.skr.registry.model.Permission;
import demo.skr.registry.model.Realm;
import lombok.extern.java.Log;
import org.skr.common.Constants;
import org.skr.registry.RegisterBatch;
import org.skr.registry.proxy.RegistryLocalProxy;
import org.skr.registry.proxy.RegistryProxy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static org.skr.common.util.CollectionUtils.list;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@SpringBootApplication(scanBasePackages = "demo.skr")
@Log
public class AioApp implements Constants {

    public static void main(String[] args) {
        SpringApplication.run(AioApp.class, args);
    }

    @Bean
    public RegistryProxy registryProxy() {
        return new RegistryLocalProxy();
    }

    //*************************************************************************
    // Application Startup Listener
    //*************************************************************************

    @Component
    public static class OnStartUpListener implements InitializingBean {

        @Autowired
        private RegistryProxy registryProxy;

        @Override
        public void afterPropertiesSet() throws Exception {
            log.info("Registering Realm demo-a ......");

            Realm realm = new Realm();
            realm.name = "demo-aio";
            realm.code = "demo-aio";
            registryProxy.registerRealm(RegisterBatch.of(realm,
                    list(
                            Permission.of("Task_Management", "Task Management"),
                            Permission.of("Task_Management_Create", "Task Management - Create"),
                            Permission.of("Task_Management_Edit", "Task Management - Edit")),
                    list(
                            EndPoint.of("Task_Management",
                                    "/tasks",
                                    "Demo-a.Task Management")
                    )
            ));

            log.info("Registering realm demo-a done!");
        }
    }
}