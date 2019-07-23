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
    public AppSvr appSvr;

    @NotNull
    @ManyToOne(optional = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT))
    public Permission permission;

    @Override
    public AppSvr getAppSvr() {
        return appSvr;
    }

    @Override
    public Permission getPermission() {
        return permission;
    }
}
