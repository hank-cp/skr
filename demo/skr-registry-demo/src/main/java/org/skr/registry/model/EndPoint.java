package org.skr.registry.model;

import lombok.Getter;
import org.skr.model.BaseEntity;
import org.skr.registry.RegistryApp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
public class EndPoint extends BaseEntity implements EndPointRegistry {

    @Id
    @NotNull
    public String url;

    @NotNull
    @OneToOne(optional = false)
    public Permission permission;

    public String breadcrumb;

    public String label;

    public String description;

    @Override
    public String getType() {
        return RegistryApp.REGISTRY_ENDPOINT;
    }
}
