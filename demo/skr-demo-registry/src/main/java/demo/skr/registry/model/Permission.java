package demo.skr.registry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import demo.skr.SimpleJwtPrincipal;
import demo.skr.registry.service.RegistryServiceImpl;
import lombok.NoArgsConstructor;
import org.skr.common.exception.ConfException;
import org.skr.common.exception.Errors;
import org.skr.common.util.tuple.Tuple3;
import org.skr.config.ApplicationContextProvider;
import org.skr.registry.model.AppSvrRegistry;
import org.skr.security.JwtPrincipal;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor
public class Permission extends demo.skr.model.registry.Permission {

    @NotNull
    @ManyToOne(optional = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT))
    public AppSvr appSvr;

    @Column(updatable = false)
    public long bit1 = 1;

    @Column(updatable = false)
    public long bit2 = 1;

    @Column(updatable = false)
    public long bit3 = 1;

    //*************************************************************************
    // Entity Listener
    //*************************************************************************

    @Override
    public AppSvrRegistry getAppSvr() {
        return appSvr;
    }

    @Override
    public void beforeRegister(AppSvrRegistry appSvr) {
        this.appSvr = (AppSvr) appSvr;
    }

    @PrePersist
    public void beforeSaved() {
        RegistryServiceImpl registryServiceImpl =
                ApplicationContextProvider.getBean(RegistryServiceImpl.class);
        Tuple3<Long, Long, Long> bits = registryServiceImpl.generatePermissionBits();
        bit1 = bits._0;
        bit2 = bits._1;
        bit3 = bits._2;
    }

    //*************************************************************************
    // Domain Methods
    //*************************************************************************

    @Override
    public PermissionResult checkAuthorization(JwtPrincipal principal) {
        SimpleJwtPrincipal jwtPrincipal;
        if (principal instanceof SimpleJwtPrincipal) {
            jwtPrincipal = (SimpleJwtPrincipal) principal;
        } else {
            throw new ConfException(Errors.NOT_SUPPORT_AUTH_PRINCIPAL
                    .setMsg("Not support auth principal %s", principal.getClass().getName()));
        }
        boolean granted = (jwtPrincipal.getPermissionBit1() & bit1) != 0
                && (jwtPrincipal.getPermissionBit2() & bit2) != 0
                && (jwtPrincipal.getPermissionBit3() & bit3) != 0;
        if (!granted) return PermissionResult.PERMISSION_DENIED;

        if (jwtPrincipal.getVipLevel() < getVipLevel())
            return PermissionResult.PERMISSION_LIMITATION;

        return PermissionResult.PERMISSION_GRANTED;
    }
}
