package demo.skr.registry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import org.skr.registry.model.RealmRegistry;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor
public class Permission extends demo.skr.model.registry.Permission {

    @NotNull
    @ManyToOne(optional = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(foreignKey = @ForeignKey(name="none", value = ConstraintMode.NO_CONSTRAINT))
    public Realm realm;

    //*************************************************************************
    // Entity Listener
    //*************************************************************************

    @Override
    public RealmRegistry getRealm() {
        return realm;
    }

    @PostLoad
    private void postLoad() {
        realmCode = realm.code;
    }

}
