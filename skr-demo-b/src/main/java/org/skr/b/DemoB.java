package org.skr.b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication(scanBasePackages = "org.skr")
@EnableWebSecurity
public class DemoB {
    public static void main(String[] args) {
        SpringApplication.run(DemoB.class, args);
    }
}

