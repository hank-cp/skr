package org.skr.security;

import javax.validation.constraints.NotNull;

public class JwtPrincipal {

    /** 0=enabled; 1=disabled*/
    public byte status;

    @NotNull
    public String username;

    public Boolean isRobot;

    public String serviceJwtToken;
}
