package org.skr.registry.model;

import org.skr.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class SiteUrl extends BaseEntity {

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

}
