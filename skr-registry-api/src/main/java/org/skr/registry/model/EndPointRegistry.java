package org.skr.registry.model;

import javax.validation.constraints.NotNull;

public interface EndPointRegistry extends Registry {

    @NotNull
    String getUrl();

    AppSvrRegistry getAppSvr();

    @NotNull
    PermissionRegistry getPermission();

    @NotNull
    String getBreadcrumb();

    String getLabel();

    String getDescription();

}
