Balabalabala......

![](docs/Saas_Architecture_v2.png?raw=true)

## Getting Start


```
-Dserver.port=8001 -Dtsf_consul_ip=192.168.1.120 -Dtsf_consul_port=8500 -Dtsf_application_id=auth -Dtsf_group_id=skr
```

## Auth Service

## Registry Service

## Convention

#### Request/Response

#### Exception
异常分为4种:
* 认证鉴权异常
* 业务逻辑异常
* Spring异常
* 未捕获异常

## 参考文档
* [Spring Cloud Feign](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html)
* [lombok](https://projectlombok.org/features/all)
* [memcached](https://github.com/memcached/memcached/wiki/Commands)
* [GraphQL Java](https://www.graphql-java.com/documentation/master/)
* [Tencent Cloud TSF](https://cloud.tencent.com/document/product/649)
