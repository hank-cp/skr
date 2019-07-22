package demo.skr.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import demo.skr.registry.model.AppSvr;
import demo.skr.registry.model.EndPoint;
import demo.skr.registry.model.Permission;
import org.skr.common.Constants;
import org.skr.registry.model.AppSvrRegistry;
import org.skr.registry.model.EndPointRegistry;
import org.skr.registry.model.PermissionRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication(scanBasePackages = "demo.skr")
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
            module.addDeserializer(AppSvrRegistry.class, new RegistryDeserializer<>(AppSvr.class));
            module.addDeserializer(PermissionRegistry.class, new RegistryDeserializer<>(Permission.class));
            module.addDeserializer(EndPointRegistry.class, new RegistryDeserializer<>(EndPoint.class));
            objectMapper.registerModule(module);
        }
    }
}
