package org.skr.security;

import javax.validation.constraints.NotNull;

public interface JwtPrincipal {

    @NotNull
    String getUsername();

    Boolean isRobot();

    String getServiceJwtToken();

    void setServiceJwtToken(String token);

}
