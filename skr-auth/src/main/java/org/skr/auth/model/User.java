package org.skr.auth.model;

import org.skr.model.IdBasedEntity;
import org.skr.security.JwtPrincipal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "org_user")
public class User extends IdBasedEntity implements JwtPrincipal {

    public static final byte USER_STATUS_JOINING_NEED_APPROVAL = 2;
    public static final byte USER_STATUS_JOINING_REJECT = 3;

    @NotNull
    @ManyToOne
    public Organization organization;

    @NotNull
    @ManyToOne
    public Account account;

    public String nickName;

    /** 0=enabled; 1=disabled; 2=apply to join org; 3=reject to join org; */
    public byte status;

    //*************************************************************************
    // Transients & Getters
    //*************************************************************************

    @Transient
    public String accessToken;

    //*************************************************************************
    // Domain Methods
    //*************************************************************************

    @Override
    public @NotNull String getUsername() {
        return account.username;
    }

    @Override
    public Boolean isRobot() {
        return false;
    }

    @Override
    public String getServiceJwtToken() {
        return accessToken;
    }

    @Override
    public void setServiceJwtToken(String token) {
        accessToken = token;
    }
}
