package demo.skr.registry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor
public class EndPoint extends demo.skr.model.registry.EndPoint {

    @NotNull
    @ManyToOne(optional = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT))
    public Realm realm;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT))
    public Permission permission;

    public boolean enabled;

    @Override
    public Realm getRealm() {
        return realm;
    }

    @Override
    public Permission getPermission() {
        return permission;
    }

    @PostLoad
    private void postLoad() {
        realmCode = realm.code;
        if (permission != null) {
            permissionCode = permission.code;
        }
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
