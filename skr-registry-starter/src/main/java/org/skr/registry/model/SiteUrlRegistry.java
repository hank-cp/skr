package org.skr.registry.model;

import org.skr.registry.model.PermissionRegistry;

import javax.validation.constraints.NotNull;

public interface SiteUrlRegistry extends Registry {

    @NotNull
    String getUrl();

    @NotNull
    PermissionRegistry getPermission();

    @NotNull
    String getNavPath();

    @NotNull
    String getLabel();

}
