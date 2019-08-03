/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skr.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.ConfException;
import org.skr.common.exception.ErrorInfo;
import org.skr.registry.proxy.RegistryProxy;
import org.skr.security.annotation.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Component
@Aspect
public class PermissionCheckingAspect {

    @Autowired
    private RegistryProxy registryProxy;

    @Around("@annotation(permission)")
    public Object check(ProceedingJoinPoint joinPoint, RequirePermission permission) throws Throwable {
        String permissionCode = permission.value();
        JwtPrincipal jwtPrincipal = JwtPrincipal.getCurrentPrincipal();
        PermissionDetail permissionDetail = registryProxy.getPermission(permissionCode);

        if (permissionDetail == null) {
            throw new ConfException(ErrorInfo.PERMISSION_NOT_FOUND
                    .setMsg("Permission %s is not found.", permissionCode));
        }

        switch (permissionDetail.checkAuthorization(jwtPrincipal)) {
            case PERMISSION_GRANTED: return joinPoint.proceed();
            case PERMISSION_DENIED:
                throw new AuthException(ErrorInfo.PERMISSION_DENIED);
            case PERMISSION_LIMITATION:
                throw new AuthException(ErrorInfo.PERMISSION_LIMITED);
            default:
                throw new ConfException(ErrorInfo.INTERNAL_SERVER_ERROR);
        }
    }
}

