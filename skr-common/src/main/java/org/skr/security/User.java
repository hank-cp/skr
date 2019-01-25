package org.skr.security;

import javax.validation.constraints.NotNull;

public class User extends JwtPrincipal {

    @NotNull
    public Organization organization;

    public String nickName;

}