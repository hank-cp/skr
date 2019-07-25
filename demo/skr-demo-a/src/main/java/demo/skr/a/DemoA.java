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

import static org.skr.common.util.CollectionUtils.list;

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
