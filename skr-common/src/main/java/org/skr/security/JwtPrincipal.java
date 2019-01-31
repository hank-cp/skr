package org.skr.security;

import javax.validation.constraints.NotNull;

public interface JwtPrincipal {

    @NotNull
    String getUsername();

    Boolean isRobot();

    String getServiceJwtToken();

    int getVipLevel();

    void setServiceJwtToken(String token);

    long getPermissionBit1();

    long getPermissionBit2();

    long getPermissionBit3();
}
