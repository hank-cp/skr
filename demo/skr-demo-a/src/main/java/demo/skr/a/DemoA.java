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
package demo.skr.a;

import demo.skr.model.registry.EndPoint;
import demo.skr.model.registry.Permission;
import demo.skr.model.registry.Realm;
import lombok.extern.slf4j.Slf4j;
import org.skr.registry.RegisterBatch;
import org.skr.registry.proxy.RegistryProxy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@SpringBootApplication(scanBasePackages = "demo.skr")
@EnableFeignClients(basePackages = {"org.skr", "demo.skr"})
@EnableDiscoveryClient
@Slf4j
public class DemoA {

    public static void main(String[] args) {
        SpringApplication.run(DemoA.class, args);
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
            realm.name = "demo-a";
            realm.code = "demo-a";
            registryProxy.registerRealm(RegisterBatch.of(realm,
                    List.of(
                        Permission.of("Task_Management", "Task Management"),
                        Permission.of("Task_Management_Create", "Task Management - Create"),
                        Permission.of("Task_Management_Edit", "Task Management - Edit")),
                    List.of(
                        EndPoint.of("Task_Management",
                                "/tasks",
                                "Demo-a.Task Management")
                    )
            ));

            log.info("Registering realm demo-a done!");
        }
    }
}
