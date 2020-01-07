package demo.skr.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import demo.skr.registry.model.PersistedPermission;
import demo.skr.registry.model.PersistedRealm;
import org.skr.config.json.CustomDeserializer;
import org.skr.permission.IPermission;
import org.skr.registry.IRealm;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@SpringBootApplication(scanBasePackages = "demo.skr")
@EnableDiscoveryClient
public class RegistryApp {

    public static void main(String[] args) {
        SpringApplication.run(RegistryApp.class, args);
    }

    @Configuration
    @AutoConfigureAfter(JacksonAutoConfiguration.class)
    public static class RegistryJsonConfigurer implements InitializingBean {

        @Autowired
        private ObjectMapper objectMapper;

        @SuppressWarnings("Duplicates")
        @Override
        public void afterPropertiesSet() {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(IRealm.class, new CustomDeserializer<>(PersistedRealm.class));
            module.addDeserializer(IPermission.class, new CustomDeserializer<>(PersistedPermission.class));
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
