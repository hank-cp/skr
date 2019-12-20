[![GitHub release](https://img.shields.io/github/release/hank-cp/skr.svg)](https://github.com/hank-cp/skr/releases)
![Maven Central](https://img.shields.io/maven-central/v/org.laxture/skr-common)
[![Github Actions](https://github.com/hank-cp/skr/workflows/Test/badge.svg)](https://github.com/hank-cp/skr/actions)
![GitHub](https://img.shields.io/github/license/hank-cp/skr.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/hank-cp/skr.svg)

Skr project is a Spring Boot/Cloud project skeleton that define abstraction 
of JWT based security configuration.
Project based on skr skeleton could define its own implementation

## Feature
* JWT based security abstraction
* Modular registration abstraction
    * Permission registration
    * EndPoint registration
* Spring Boot Starter 
* Switchable Spring Cloud platform
    * [Self-Setup Spring Cloud](https://spring.io/projects/spring-cloud)
    * [Tencent TSF](https://cloud.tencent.com/product/tsf)
    
## Authentication flows
![](docs/sign_up.png)
![](docs/sign_in.png)
![](docs/request.png)

## Terminology
* Principal
    * UserPrincipal
    * JwtPrincipal
* Certification
* Tokens
    * access-token:
    * refresh-token:
    * login-token:
    * robot-token:
    * train-token:

## Auth Service
TBD

## Registry Service
TBD

## Switch Spring Cloud provider
TBD

## Convention

#### Exception
* [ErrorInfo](skr-common/src/main/java/org/skr/common/exception/ErrorInfo.java)
* [AuthException](skr-common/src/main/java/org/skr/common/exception/AuthException.java)
* [BizException](skr-common/src/main/java/org/skr/common/exception/BizException.java)
* [ConfException](skr-common/src/main/java/org/skr/common/exception/ConfException.java)