package org.skr.registry.model;

import org.skr.security.PermissionDetail;

public interface PermissionRegistry extends PermissionDetail, Registry {

    AppSvrRegistry getAppSvr();

    void setAppSvr(AppSvrRegistry appSvr);

}
