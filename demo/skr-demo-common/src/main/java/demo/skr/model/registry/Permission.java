package demo.skr.model.registry;

import demo.skr.model.CodeBasedEntity;
import lombok.Getter;
import org.skr.registry.model.AppSvrRegistry;
import org.skr.registry.model.PermissionRegistry;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@Getter
public class Permission extends CodeBasedEntity implements PermissionRegistry {

    @NotNull
    @Column(updatable = false)
    public String appSvrCode;

    @NotNull
    public String name;

    public int vipLevel;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public AppSvrRegistry getAppSvr() {
        AppSvr appSvr = new AppSvr();
        appSvr.code = appSvrCode;
        return appSvr;
    }

    @Override
    public void beforeRegister(AppSvrRegistry appSvr) {
    }

    public static Permission of(@NotNull String code,
                                @NotNull String name) {
        Permission permission = new Permission();
        permission.code = code;
        permission.name = name;
        return permission;
    }
}
