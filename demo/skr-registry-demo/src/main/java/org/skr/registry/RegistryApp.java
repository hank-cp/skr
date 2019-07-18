package org.skr.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.skr.common.Constants;
import org.skr.registry.model.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication(scanBasePackages = "org.skr")
public class RegistryApp implements Constants {

    public static void main(String[] args) {
        SpringApplication.run(RegistryApp.class, args);
    }

    //*************************************************************************
    // Application Startup Listener
    //*************************************************************************

    @Component
    public static class OnStartUpListener implements InitializingBean {

        @Autowired
        private ObjectMapper objectMapper;

        @Override
        public void afterPropertiesSet() {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(AppSvrRegistry.class, new RegistryDeserializer<AppSvr>());
            module.addDeserializer(PermissionRegistry.class, new RegistryDeserializer<Permission>());
            module.addDeserializer(EndPointRegistry.class, new RegistryDeserializer<EndPoint>());
            objectMapper.registerModule(module);
        }
    }
}
