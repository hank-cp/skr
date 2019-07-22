package org.skr.security;

import javax.validation.constraints.NotNull;

public interface JwtPrincipal {

    @NotNull
    String getUsername();

    Boolean isRobot();

    String getApiTrainJwtToken();

    void setApiTrainJwtToken(String token);
}
