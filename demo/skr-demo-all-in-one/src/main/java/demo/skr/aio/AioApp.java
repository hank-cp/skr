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

import demo.skr.reg.PermRegService;
import demo.skr.reg.PermRegistryPack;
import demo.skr.reg.model.EndPoint;
import demo.skr.reg.model.Permission;
import demo.skr.registry.service.PermRegHost;
import lombok.extern.java.Log;
import org.skr.registry.SimpleRealm;
import org.skr.security.PermissionServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@SpringBootApplication(scanBasePackages = "demo.skr")
@Log
public class AioApp {

    public static void main(String[] args) {
        SpringApplication.run(AioApp.class, args);
    }

    //*************************************************************************
    // Application Startup Listener
    //*************************************************************************

    @Autowired
    private PermRegHost permRegHost;

    @Bean
    public PermissionServiceClient permissionService() {
        return code -> permRegHost.getPermission(code);
    }

    @Component
    public static class OnStartUpListener implements ApplicationListener<ApplicationStartedEvent> {

        @Autowired
        private PermRegService permRegService;

        @Override
        public void onApplicationEvent(ApplicationStartedEvent event) {
            log.info("Registering Realm aio ......");

            SimpleRealm realm = SimpleRealm.of("demo");
            PermRegistryPack permRegistryPack = new PermRegistryPack();
            permRegistryPack.permissions = List.of(
                    Permission.of("Task", "Task"),
                    Permission.of("Task_Create", "Task - Create"),
                    Permission.of("Task_Edit", "Task - Edit"));
            permRegistryPack.endPoints = List.of(
                    EndPoint.of("Task", "/tasks", "Demo-a.Task"));

            permRegService.register(realm.code, realm.version, permRegistryPack);

            log.info("Registering realm aio done!");
        }
    }
//
//    @Configuration
//    public class SpringRootConfig {
//
//        @Autowired
//        DataSource dataSource;
//
//        //default username : sa, password : ''
//        @PostConstruct
//        public void getDbManager(){
//            DatabaseManagerSwing.main(
//                    new String[] { "--url", "jdbc:hsqldb:mem:local", "--user", "sa", "--password", ""});
//        }
//    }
}