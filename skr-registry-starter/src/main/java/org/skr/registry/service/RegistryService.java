package org.skr.registry.service;

import org.skr.common.util.tuple.Tuple3;
import org.skr.registry.model.AppSvrRegistry;
import org.skr.registry.model.EndPointRegistry;
import org.skr.registry.model.PermissionRegistry;
import org.springframework.stereotype.Service;

@Service
public interface RegistryService<
        AppSvr extends AppSvrRegistry,
        Permission extends PermissionRegistry,
        EndPoint extends EndPointRegistry> {

    Tuple3<Long, Long, Long> generatePermissionBits();

    AppSvr getAppSvr(String code);

    AppSvr saveAppSvr(AppSvr saving, AppSvr existed);

    Permission getPermission(String code);

    Permission savePermission(Permission saving, Permission existed);

    EndPoint getEndPoint(String url);

    EndPoint saveEndPoint(EndPoint saving, EndPoint existed);

}
