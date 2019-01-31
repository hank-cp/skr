package org.skr.a;

import lombok.extern.slf4j.Slf4j;
import org.skr.security.appsvr.RegistryClient;
import org.skr.security.appsvr.RegistryClient.AppSvr;
import org.skr.security.appsvr.RegistryClient.Permission;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import static org.skr.common.util.CollectionUtils.list;

@SpringBootApplication(scanBasePackages = "org.skr")
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
        private RegistryClient registryClient;

        @Override
        public void afterPropertiesSet() throws Exception {
            log.info("Registering AppSvr demo-a ......");

            AppSvr appSvr =
                    AppSvr.of("demo-a", "demo-a");
            registryClient.registerAppSvr(appSvr);

            registryClient.registerPermission("demo-a", list(
                    Permission.of("Task_Management", "任务管理"),
                    Permission.of("Task_Management_Create", "任务管理-新建"),
                    Permission.of("Task_Management_Edit", "任务管理-修改")
            ));

            registryClient.registerSiteUrl("demo-a", list(
                    RegistryClient.SiteUrl.of("/tasks",
                            Permission.of("Task.Management", ""),
                            "Demo-a,任务管理", "任务管理")
            ));

            log.info("Registering AppSvr demo-a done!");
        }
    }
}
