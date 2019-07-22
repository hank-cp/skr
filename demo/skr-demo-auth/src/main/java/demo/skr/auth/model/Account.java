package demo.skr.auth.model;

import demo.skr.model.BaseEntity;
import org.skr.common.Constants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
public class Account extends BaseEntity implements UserDetails {

    @Id
    @NotNull
    public String username;

    @NotNull
    public String password;

    /** 0=enabled; 1=disabled; 2=fault state; */
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
