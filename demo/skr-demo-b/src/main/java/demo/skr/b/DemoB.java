package demo.skr.b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "demo.skr")
@EnableFeignClients(basePackages = {"org.skr", "demo.skr"})
public class DemoB {
    public static void main(String[] args) {
        SpringApplication.run(DemoB.class, args);
    }
}

