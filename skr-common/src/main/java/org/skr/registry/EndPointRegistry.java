package org.skr.registry;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface EndPointRegistry extends Registry {

    String BREADCRUMB_SEPARATOR = "\\.";

    @NotNull
    String getUrl();

    @NotNull
    RealmRegistry getRealm();

    PermissionRegistry getPermission();

    List<String> getRelatedPermissionCodes();

    @NotNull
    String getBreadcrumb();

    String getLabel();

    String getDescription();

}
