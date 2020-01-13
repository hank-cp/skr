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

import demo.skr.reg.PermRegService;
import demo.skr.reg.PermRegistryPack;
import demo.skr.reg.model.EndPoint;
import demo.skr.reg.model.Permission;
import lombok.extern.slf4j.Slf4j;
import org.skr.registry.SimpleRealm;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationListener;
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
    public static class OnStartUpListener implements ApplicationListener<ApplicationReadyEvent> {

        @Autowired
        private PermRegService permRegService;

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            log.info("Registering Realm demo-a ......");

            SimpleRealm realm = SimpleRealm.of("demo-a");
            PermRegistryPack permRegistryPack = new PermRegistryPack();
            permRegistryPack.permissions = List.of(
                    Permission.of("Task", "Task"),
                    Permission.of("Task_Create", "Task - Create"),
                    Permission.of("Task_Edit", "Task - Edit"));
            permRegistryPack.endPoints = List.of(
                    EndPoint.of("Task", "/tasks", "Demo-a.Task"));

            permRegService.register(realm.code, realm.version, permRegistryPack);

            log.info("Registering realm demo-a done!");
        }
    }
}
