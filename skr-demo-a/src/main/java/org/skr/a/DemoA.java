package org.skr.a;

import org.skr.a.appsvr.RegistryClient;
import org.skr.common.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static org.skr.common.util.CollectionUtils.*;

@SpringBootApplication(scanBasePackages = "org.skr")
public class DemoA {
    public static void main(String[] args) {
        SpringApplication.run(DemoA.class, args);
    }

    //*************************************************************************
    // Application Startup Listener
    //*************************************************************************

    @Component
    public static class OnStartUpListener {

        @Autowired
        private RegistryClient registryClient;

        @PostConstruct
        public void onStartUp() {
//            event.
//            RegistryClient registryClient = event.getApplicationContext().getBean(RegistryClient.class);

            registryClient.registerAppSvr(map(
                    entry("code", "demo-a"),
                    entry("name", "demo-a")
            ));

            registryClient.registerPermission("demo-a", list(
                    map(
                        entry("appSvr", map(
                                entry("code", "demo-a")
                        )),
                        entry("code", "Task.Management"),
                        entry("name", "Task Management")
                    ), map(
                        entry("appSvr", map(
                                entry("code", "demo-a")
                        )),
                        entry("code", "Task.Management.Create"),
                        entry("name", "Create Task")
                    ), map(
                        entry("appSvr", map(
                                entry("code", "demo-a")
                        )),
                        entry("code", "Task.Management.Edit"),
                        entry("name", "Edit Task")
                    )
            ));
        }
    }
}
