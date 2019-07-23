package demo.skr.model.registry;

import demo.skr.model.BaseEntity;
import lombok.Getter;
import org.skr.common.util.Checker;
import org.skr.registry.model.EndPointRegistry;
import org.skr.registry.model.PermissionRegistry;
import org.skr.registry.model.RealmRegistry;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@Getter
public class EndPoint extends BaseEntity implements EndPointRegistry {

    public static final String REGISTRY_ENDPOINT = "EndPoint";

    @Id
    @NotNull
    public String url;

    @NotNull
    @Transient
    public String realmCode;

    @Transient
    public String permissionCode;

    @NotNull
    public String breadcrumb;

    public String label;

    public String description;

    @Override
    public RealmRegistry getRealm() {
        Realm realm = new Realm();
        realm.code = realmCode;
        return realm;
    }

    @Override
    public @NotNull PermissionRegistry getPermission() {
        if (Checker.isEmpty(permissionCode)) return null;
        Permission permission = new Permission();
        permission.code = permissionCode;
        return permission;
    }

    public static EndPoint of(String permissionCode,
                              @NotNull String url,
                              @NotNull String breadcrumb) {
        EndPoint endPoint = new EndPoint();
        endPoint.url = url;
        endPoint.permissionCode = permissionCode;
        endPoint.breadcrumb = breadcrumb;
        return endPoint;
    }

}
