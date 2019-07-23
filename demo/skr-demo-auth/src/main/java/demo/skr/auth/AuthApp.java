package demo.skr.auth;

import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@SpringBootApplication(scanBasePackages = "demo.skr")
@EnableWebSecurity
public class AuthApp {
    public static void main(String[] args) {
        SpringApplication.run(AuthApp.class, args);
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
