package org.skr.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.skr.common.Constants;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.ConfException;
import org.skr.common.exception.Errors;
import org.skr.config.ApplicationContextProvider;
import org.skr.security.annotation.RequirePermission;
import org.skr.security.feign.RegistryClient;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class PermissionCheckingAspect {

    @Pointcut("@annotation(org.skr.security.annotation.RequirePermission)")
    private void cut() {}

    @Around("cut()&&@annotation(permission)")
    public Object check(ProceedingJoinPoint joinPoint, RequirePermission permission) throws Throwable {
        String permissionCode = permission.value();
        JwtPrincipal jwtPrincipal = ApplicationContextProvider.getCurrentPrincipal();
        RegistryClient registryClient = ApplicationContextProvider.getBean(RegistryClient.class);
        PermissionDetail permissionDetail = registryClient.getPermission(permissionCode);

        if (permissionDetail == null) {
            throw new ConfException(Errors.PERMISSION_NOT_FOUND
                    .setMsg("Permission %s is not found.", permissionCode));
        }

        switch (permissionDetail.checkAuthorization(jwtPrincipal)) {
            case Constants.PERMISSION_GRANTED: return joinPoint.proceed();
            case Constants.PERMISSION_DENIED:
                throw new AuthException(Errors.PERMISSION_DENIED);
            case Constants.PERMISSION_LIMITATION:
                throw new AuthException(Errors.PERMISSION_LIMITED);
            default:
                throw new ConfException(Errors.INTERNAL_SERVER_ERROR);
        }
    }


}

