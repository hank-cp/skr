package org.skr.a;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication(scanBasePackages = "org.skr")
@EnableWebSecurity
public class DemoA {
    public static void main(String[] args) {
        SpringApplication.run(DemoA.class, args);
    }
}
