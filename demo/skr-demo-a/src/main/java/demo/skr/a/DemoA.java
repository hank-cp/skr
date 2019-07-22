package demo.skr.a;

import demo.skr.model.registry.AppSvr;
import demo.skr.model.registry.EndPoint;
import demo.skr.model.registry.Permission;
import lombok.extern.slf4j.Slf4j;
import org.skr.registry.feign.RegistryClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

import static org.skr.common.util.CollectionUtils.list;

@SpringBootApplication(scanBasePackages = "demo.skr")
@EnableFeignClients(basePackages = {"org.skr", "demo.skr"})
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

            AppSvr appSvr = new AppSvr();
            appSvr.name = "demo-a";
            appSvr.code = "demo-a";
            registryClient.registerAppSvr(appSvr);

            registryClient.registerPermission("demo-a", list(
                    Permission.of("Task_Management", "Task Management"),
                    Permission.of("Task_Management_Create", "Task Management - Create"),
                    Permission.of("Task_Management_Edit", "Task Management - Edit")
            ));

            registryClient.registerEndPoint("demo-a", list(
                    EndPoint.of("demo-a",
                            "Task_Management",
                            "/tasks",
                            "Demo-a.Task Management")
            ));

            log.info("Registering AppSvr demo-a done!");
        }
    }
}
