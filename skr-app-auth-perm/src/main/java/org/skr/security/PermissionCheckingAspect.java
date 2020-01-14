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
import org.skr.common.exception.PermissionException;
import org.skr.security.annotation.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Component
@Aspect
@ConditionalOnProperty(prefix = "spring.skr.security", name = "permission-check-enabled", havingValue = "true", matchIfMissing = true)
public class PermissionCheckingAspect {

    @Autowired
    private PermissionServiceClient permissionService;

    @Around("@annotation(permission)")
    public Object check(ProceedingJoinPoint joinPoint, RequirePermission permission) throws Throwable {
        String permissionKey = permission.value();
        Optional<JwtPrincipal> jwtPrincipal = JwtPrincipal.getCurrentPrincipal();

        if (jwtPrincipal.isEmpty()) {
            throw new AuthException(ErrorInfo.AUTHENTICATION_REQUIRED);
        }

        PermissionDetail permissionDetail = permissionService.getPermission(permissionKey);
        if (permissionDetail == null) {
            throw new ConfException(ErrorInfo.PERMISSION_NOT_FOUND.msgArgs(permissionKey));
        }

        switch (permissionDetail.checkAuthorization(jwtPrincipal.get())) {
            case PERMISSION_GRANTED: return joinPoint.proceed();
            case PERMISSION_DENIED:
                throw new PermissionException(ErrorInfo.PERMISSION_DENIED);
            case PERMISSION_LIMITED:
                throw new PermissionException(ErrorInfo.PERMISSION_LIMITED);
            default:
                throw new ConfException(ErrorInfo.INTERNAL_SERVER_ERROR);
        }
    }
}

