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
package demo.skr.auth;

import com.pszymczyk.consul.ConsulProcess;
import com.pszymczyk.consul.ConsulStarterBuilder;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@SpringBootApplication(scanBasePackages = "demo.skr")
@EnableDiscoveryClient
public class AuthApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(AuthApp.class)
                .initializers(new EmbeddedConsul()).run(args);
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Configuration
    @ConditionalOnProperty(value = "lolth.embedded-consul", havingValue = "true")
    public static class EmbeddedConsul implements
            ApplicationContextInitializer<ConfigurableApplicationContext>, DisposableBean, Ordered {

        private ConsulProcess consul;

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            if (!Objects.equals(applicationContext.getEnvironment()
                    .getProperty("lolth.embedded-consul"), "true")) return;
            try {
                FileUtils.forceMkdir(new File("tmp"));
            } catch (IOException ignored) {}
            consul = ConsulStarterBuilder.consulStarter()
                    .withHttpPort(Integer.parseInt(applicationContext.getEnvironment()
                            .getProperty("spring.cloud.consul.port", "8500")))
                    .withConsulVersion("1.6.0")
                    .withConsulBinaryDownloadDirectory(Path.of("tmp"))
                    .build().start();
        }

        @Override
        public void destroy() {
            if (consul != null) consul.close();
        }
    }
}
