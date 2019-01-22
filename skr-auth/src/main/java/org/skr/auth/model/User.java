package org.skr.auth.model;

import org.skr.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "org_user")
public class User extends BaseEntity {

    public static final byte USER_STATUS_JOINING_NEED_APPROVAL = 2;
    public static final byte USER_STATUS_JOINING_REJECT = 3;

    @NotNull
    @ManyToOne
    public Organization organization;

    @NotNull
    @ManyToOne
    public Account account;

    public String nickName;

    /** 状态 0=正常; 1=禁用; 2=申请加入组织待批准; 3=申请加入组织被拒绝 */
    public byte status;

    //*************************************************************************
    // Domain Methods
    //*************************************************************************

    public org.skr.security.User buildCommonUser() {
        org.skr.security.User commonUser = new org.skr.security.User();
        // TODO set organization
//        commonUser.organization = organization;
        commonUser.username = account.username;
        commonUser.nickName = nickName;
        return commonUser;
    }

}
