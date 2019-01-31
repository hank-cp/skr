package org.skr.auth.model;

import org.skr.model.IdBasedEntity;
import org.skr.security.JwtPrincipal;
import org.skr.security.SimpleJwtPrincipal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "org_user")
public class User extends IdBasedEntity {

    public static final byte USER_STATUS_JOINING_NEED_APPROVAL = 2;
    public static final byte USER_STATUS_JOINING_REJECT = 3;

    @NotNull
    @ManyToOne
    public Organization organization;

    @NotNull
    @ManyToOne
    public Account account;

    private long permissionBit1;

    private long permissionBit2;

    private long permissionBit3;

    public String nickName;

    /** 0=enabled; 1=disabled; 2=apply to join org; 3=reject to join org; */
    public byte status;

    public JwtPrincipal buildJwtPrincipal() {
        return SimpleJwtPrincipal.of(
                account.username, permissionBit1, permissionBit2, permissionBit3,
                nickName, status, null, false, organization.vipLevel);

    }
}
