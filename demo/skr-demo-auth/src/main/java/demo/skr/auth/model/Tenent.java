package demo.skr.auth.model;

import demo.skr.model.CodeBasedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class Tenent extends CodeBasedEntity {

    @NotNull
    @Column(unique = true)
    public String name;

    /** Paying level */
    public int vipLevel;

}
