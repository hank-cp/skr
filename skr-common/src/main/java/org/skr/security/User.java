package org.skr.security;

import javax.validation.constraints.NotNull;

public class User {

    /** 0=enabled; 1=disabled*/
    public byte status;

    @NotNull
    public Organization organization;

    @NotNull
    public String username;

    public String nickName;

    public Boolean isRobot;
}
