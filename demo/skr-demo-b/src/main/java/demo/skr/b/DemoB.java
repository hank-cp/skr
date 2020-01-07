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
package demo.skr.b;

import demo.skr.a.TaskRegistryPack;
import demo.skr.a.model.TaskExtension;
import demo.skr.reg.PermRegistryPack;
import demo.skr.reg.model.EndPoint;
import demo.skr.reg.model.Permission;
import lombok.extern.slf4j.Slf4j;
import org.skr.registry.SimpleRealm;
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
public class DemoB {

    public static void main(String[] args) {
        SpringApplication.run(DemoB.class, args);
    }

    @Component
    public static class OnStartUpListener implements InitializingBean {

        @Autowired
        private PermRegServiceClient permRegService;

        @Autowired
        private TaskRegServiceClient taskRegService;

        @Override
        public void afterPropertiesSet() throws Exception {
            log.info("Registering Realm demo-b ......");

            SimpleRealm realm = SimpleRealm.of("demo-b");
            PermRegistryPack permRegistryPack = new PermRegistryPack();
            permRegistryPack.permissions = List.of(
                    Permission.of("Task_Record", "Task Record"));
            permRegistryPack.endPoints = List.of(
                    EndPoint.of("Task_Record", "/tasks", "Demo-a.Task Record"));
            permRegService.register(realm.code, realm.version, permRegistryPack);

            TaskRegistryPack taskRegistryPack = new TaskRegistryPack();
            taskRegistryPack.taskExtensions = List.of(
                    TaskExtension.of("Task_Record"));
            taskRegService.register(realm.code, realm.version, taskRegistryPack);

            log.info("Registering realm demo-b done!");
        }
    }
}

