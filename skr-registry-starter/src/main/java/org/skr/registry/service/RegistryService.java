package org.skr.registry.service;

import org.skr.common.util.tuple.Tuple3;
import org.skr.registry.model.AppSvrRegistry;
import org.skr.registry.model.PermissionRegistry;
import org.skr.registry.model.SiteUrlRegistry;
import org.springframework.stereotype.Service;

@Service
public interface RegistryService<
        AppSvr extends AppSvrRegistry,
        Permission extends PermissionRegistry,
        SiteUrl extends SiteUrlRegistry> {

    Tuple3<Long, Long, Long> generatePermissionBits();

    AppSvr getAppSvr(String code);

    AppSvr saveAppSvr(AppSvr saving, AppSvr existed);

    Permission getPermission(String code);

    Permission savePermission(Permission saving, Permission existed);

    SiteUrl getSiteUrl(String url);

    SiteUrl saveSiteUrl(SiteUrl saving, SiteUrl existed);

}
