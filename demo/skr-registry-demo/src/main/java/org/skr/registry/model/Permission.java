package org.skr.registry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.skr.common.util.tuple.Tuple3;
import org.skr.config.ApplicationContextProvider;
import org.skr.model.CodeBasedEntity;
import org.skr.registry.RegistryApp;
import org.skr.registry.service.RegistryServiceImpl;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@EntityListeners(Permission.EntityListener.class)
@Getter
public class Permission extends CodeBasedEntity implements PermissionRegistry {

    @NotNull
    @ManyToOne(optional = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public AppSvr appSvr;

    @NotNull
    public String name;

    @Column(updatable = false)
    private long bit1 = 1;

    @Column(updatable = false)
    private long bit2 = 1;

    @Column(updatable = false)
    private long bit3 = 1;

    public int vipLevel;

    @Override
    public void setAppSvr(AppSvrRegistry appSvr) {
        this.appSvr = (AppSvr) appSvr;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getType() {
        return RegistryApp.REGISTRY_PERMISSION;
    }

//*************************************************************************
    // Entity Listener
    //*************************************************************************

    public static class EntityListener {
        @PrePersist
        public void beforeSaved(Permission permission) {
            RegistryServiceImpl registryServiceImpl =
                    ApplicationContextProvider.getBean(RegistryServiceImpl.class);
            Tuple3<Long, Long, Long> bits = registryServiceImpl.generatePermissionBits();
            permission.bit1 = bits._0;
            permission.bit2 = bits._1;
            permission.bit3 = bits._2;
        }
    }

}
