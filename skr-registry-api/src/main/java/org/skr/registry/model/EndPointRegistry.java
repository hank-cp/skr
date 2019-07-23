package org.skr.registry.model;

import javax.validation.constraints.NotNull;

public interface EndPointRegistry extends Registry {

    String BREADCRUMB_SEPARATOR = "\\.";

    @NotNull
    String getUrl();

    @NotNull
    RealmRegistry getRealm();

    PermissionRegistry getPermission();

    @NotNull
    String getBreadcrumb();

    String getLabel();

    String getDescription();

}
