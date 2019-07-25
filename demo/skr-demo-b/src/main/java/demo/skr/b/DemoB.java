package demo.skr.b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "demo.skr")
@EnableFeignClients(basePackages = {"org.skr", "demo.skr"})
@EnableDiscoveryClient
public class DemoB {
    public static void main(String[] args) {
        SpringApplication.run(DemoB.class, args);
    }
}

