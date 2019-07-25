Skr project is a Spring Boot/Cloud project skeleton that define abstraction 
of security and modular registration, as well as built-in integration.
Project based on skr skeleton could define its own implementation 

## Feature
* JWT based Security Abstraction
* Modular Registration Abstraction

## Getting Start with Spring Cloud
* Install consul
* Start consul 
    ```
    consul agent -dev
    ```
* Start 
```
-Dserver.port=8001 -Dtsf_consul_ip=192.168.1.120 -Dtsf_consul_port=8500 -Dtsf_application_id=auth -Dtsf_group_id=skr
```

## Getting Start with Spring Boot (all in one monolithic)

## Auth Service

## Registry Service

## Convention

#### Request/Response

#### Exception
* [AuthException](skr-common/src/main/java/org/skr/common/exception/AuthException.java)
* [BizException](skr-common/src/main/java/org/skr/common/exception/BizException.java)
* [ConfigurationException](skr-common/src/main/java/org/skr/common/exception/ConfException.java)

## 参考文档
* [Spring Cloud Feign](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html)
* [lombok](https://projectlombok.org/features/all)
* [memcached](https://github.com/memcached/memcached/wiki/Commands)
* [GraphQL Java](https://www.graphql-java.com/documentation/master/)
* [Tencent Cloud TSF](https://cloud.tencent.com/document/product/649)
