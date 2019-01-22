package org.skr.auth.model;

import org.skr.common.Constants;
import org.skr.model.BaseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
public class Account extends BaseEntity implements UserDetails {

    @NotNull
    @Column(unique = true)
    public String username;

    @NotNull
    public String password;

    /** 状态 0=启用; 1=禁用; 2=状态异常 */
    public byte status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == Constants.ENABLED;
    }
}
