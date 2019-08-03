package org.skr.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.ConfException;
import org.skr.common.exception.ErrorInfo;
import org.skr.config.ApplicationContextProvider;
import org.skr.registry.proxy.RegistryProxy;
import org.skr.security.annotation.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

