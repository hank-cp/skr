package org.skr.registry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.skr.common.util.tuple.Tuple3;
import org.skr.config.ApplicationContextProvider;
import org.skr.model.CodeBasedEntity;
import org.skr.registry.service.PermissionService;
import org.skr.security.PermissionDetail;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@EntityListeners(Permission.EntityListener.class)
public class Permission extends CodeBasedEntity implements PermissionDetail {

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
    public String getKey() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getBit1() {
        return bit1;
    }

    @Override
    public long getBit2() {
        return bit2;
    }

    @Override
    public long getBit3() {
        return bit3;
    }

    @Override
    public int getVipLevel() {
        return vipLevel;
    }

    //*************************************************************************
    // Entity Listener
    //*************************************************************************

    public static class EntityListener {
        @PrePersist
        public void beforeSaved(Permission permission) {
            PermissionService permissionService =
                    ApplicationContextProvider.getBean(PermissionService.class);
            Tuple3<Long, Long, Long> bits = permissionService.generatePermissionBits();
            permission.bit1 = bits._0;
            permission.bit2 = bits._1;
            permission.bit3 = bits._2;
        }
    }

}
