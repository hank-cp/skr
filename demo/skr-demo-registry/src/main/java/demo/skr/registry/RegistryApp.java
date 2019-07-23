package demo.skr.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import demo.skr.RegistryDeserializer;
import demo.skr.registry.model.Realm;
import demo.skr.registry.model.EndPoint;
import demo.skr.registry.model.Permission;
import org.skr.common.Constants;
import org.skr.registry.model.RealmRegistry;
import org.skr.registry.model.EndPointRegistry;
import org.skr.registry.model.PermissionRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(scanBasePackages = "demo.skr")
public class RegistryApp implements Constants {

    public static void main(String[] args) {
        SpringApplication.run(RegistryApp.class, args);
    }

    //*************************************************************************
    // Application Startup Listener
    //*************************************************************************

    @Configuration
    @AutoConfigureAfter(JacksonAutoConfiguration.class)
    public static class OnStartUpListener implements InitializingBean {

        @Autowired
        private ObjectMapper objectMapper;

        @SuppressWarnings("Duplicates")
        @Override
        public void afterPropertiesSet() {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(RealmRegistry.class, new RegistryDeserializer<>(Realm.class));
            module.addDeserializer(PermissionRegistry.class, new RegistryDeserializer<>(Permission.class));
            module.addDeserializer(EndPointRegistry.class, new RegistryDeserializer<>(EndPoint.class));
            objectMapper.registerModule(module);
        }
    }

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
