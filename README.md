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
    * Extensible registration
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
* Realm
* Permission
* EndPoint

## Libraries
* skr-common: Common model and utils.
* skr-cloud-spring: Default official Spring-Cloud implementation
    * skr-cloud-tsf: Tencent Spring-Cloud implementation
    
### Security
* skr-service-auth: Authentication manager service depends on this. 
* skr-app-auth: Services need authentication depend on this.
* skr-app-auth-perm: Services need permission checking depend on this.

### Registration
* skr-service-reg: Registration center service depends on this.

## How To
### Auth Service
TBD

### Registry Service
TBD

### Switch Spring Cloud provider
TBD

#### Exception
* [ErrorInfo](skr-common/src/main/java/org/skr/common/exception/ErrorInfo.java)
* [AuthException](skr-common/src/main/java/org/skr/common/exception/AuthException.java)
* [BizException](skr-common/src/main/java/org/skr/common/exception/BizException.java)
* [ConfException](skr-common/src/main/java/org/skr/common/exception/ConfException.java)

## Integration Test
1. Start this service in sequence:
    1. AuthApp
    2. RegistryApp
    3. DemoA
    4. DemoB
2. run `integration-test/run_test.sh`