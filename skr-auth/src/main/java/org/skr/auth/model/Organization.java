package org.skr.auth.model;

import org.skr.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class Organization extends BaseEntity {

    @NotNull
    @Column(unique = true)
    public String code;

    @NotNull
    @Column(unique = true)
    public String name;

    /** Paying level */
    public int vipLevel;

}
