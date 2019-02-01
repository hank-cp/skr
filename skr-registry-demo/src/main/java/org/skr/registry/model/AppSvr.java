package org.skr.registry.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Getter;
import org.skr.model.CodeBasedEntity;
import org.skr.registry.RegistryApp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import static org.skr.common.Constants.*;

@Entity
@Getter
//@JsonSubTypes({
//        @JsonSubTypes.Type(value=AppSvr.class,      name=REGISTRY_APPSVR),
//        @JsonSubTypes.Type(value=Permission.class,  name=REGISTRY_PERMISSION),
//        @JsonSubTypes.Type(value=SiteUrl.class,     name=REGISTRY_SITEURL)
//})
public class AppSvr extends CodeBasedEntity implements AppSvrRegistry {

    @NotNull
    @Column(unique = true)
    public String name;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getType() {
        return RegistryApp.REGISTRY_APPSVR;
    }
}
