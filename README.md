[![GitHub release](https://img.shields.io/github/release/hank-cp/skr.svg)](https://github.com/hank-cp/skr/releases)
[![Github Actions](https://github.com/hank-cp/skr/workflows/Test/badge.svg)](https://github.com/hank-cp/skr/actions)
![GitHub](https://img.shields.io/github/license/hank-cp/skr.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/hank-cp/skr.svg)

Skr project is a Spring Boot/Cloud project skeleton that define abstraction 
of JWT based security configuration.
Project based on skr skeleton could define its own implementation 

## Feature
* JWT based Security Abstraction
* Modular Registration Abstraction
* Spring Boot starter 
* Switch Spring Cloud platform
    * [Self-Setup Spring Cloud](https://spring.io/projects/spring-cloud)
    * [Tencent TSF](https://cloud.tencent.com/product/tsf)

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

## Switch Spring Cloud provider

## Convention

#### Exception
* [AuthException](skr-common/src/main/java/org/skr/common/exception/AuthException.java)
* [BizException](skr-common/src/main/java/org/skr/common/exception/BizException.java)
* [ConfigurationException](skr-common/src/main/java/org/skr/common/exception/ConfException.java)

<!--
## Reference
* [Spring Cloud Feign](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html)
* [lombok](https://projectlombok.org/features/all)
* [memcached](https://github.com/memcached/memcached/wiki/Commands)
* [GraphQL Java](https://www.graphql-java.com/documentation/master/)
* [Tencent Cloud TSF](https://cloud.tencent.com/document/product/649)
-->