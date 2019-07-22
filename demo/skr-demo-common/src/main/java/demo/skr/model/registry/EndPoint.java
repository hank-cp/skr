package demo.skr.model.registry;

import demo.skr.model.BaseEntity;
import lombok.Getter;
import org.skr.registry.model.AppSvrRegistry;
import org.skr.registry.model.EndPointRegistry;
import org.skr.registry.model.PermissionRegistry;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@Getter
public class EndPoint extends BaseEntity implements EndPointRegistry {

    public static final String REGISTRY_ENDPOINT = "EndPoint";

    @Id
    @NotNull
    public String url;

    @NotNull
    @Column(updatable = false)
    public String appSvrCode;

    @NotNull
    @Column(updatable = false)
    public String permissionCode;

    @NotNull
    public String breadcrumb;

    public String label;

    public String description;

    @Override
    public AppSvrRegistry getAppSvr() {
        AppSvr appSvr = new AppSvr();
        appSvr.code = appSvrCode;
        return appSvr;
    }

    @Override
    public @NotNull PermissionRegistry getPermission() {
        Permission permission = new Permission();
        permission.code = permissionCode;
        return permission;
    }

    public static EndPoint of(@NotNull String appSvrCode,
                              @NotNull String permissionCode,
                              @NotNull String url,
                              @NotNull String breadcrumb) {
        EndPoint endPoint = new EndPoint();
        endPoint.url = url;
        endPoint.appSvrCode = appSvrCode;
        endPoint.permissionCode = permissionCode;
        endPoint.breadcrumb = breadcrumb;
        return endPoint;
    }

}
