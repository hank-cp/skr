package org.skr.registry.model;

import lombok.Getter;
import org.skr.model.BaseEntity;
import org.skr.registry.RegistryApp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
public class SiteUrl extends BaseEntity implements SiteUrlRegistry {

    @Id
    @NotNull
    public String url;

    @NotNull
    @OneToOne(optional = false)
    public Permission permission;

    @NotNull
    public String navPath;

    @NotNull
    @Column(unique = true)
    public String label;

    @Override
    public String getType() {
        return RegistryApp.REGISTRY_SITEURL;
    }
}
