package org.skr.registry.model;

import org.skr.model.CodeBasedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class AppSvr extends CodeBasedEntity {

    @NotNull
    @Column(unique = true)
    public String name;

}
