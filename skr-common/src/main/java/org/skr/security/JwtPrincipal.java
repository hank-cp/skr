package org.skr.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.NotNull;

public interface JwtPrincipal {

    @NotNull
    String getUsername();

    Boolean isRobot();

    String getApiTrainJwtToken();

    void setApiTrainJwtToken(String token);

    static JwtPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtPrincipal user = null;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof JwtPrincipal) {
            user = (JwtPrincipal) authentication.getPrincipal();
        }
        return user;
    }
}
