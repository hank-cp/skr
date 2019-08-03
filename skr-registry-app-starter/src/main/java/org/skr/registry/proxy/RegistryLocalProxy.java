
package org.skr.registry.proxy;

import org.skr.registry.EndPointRegistry;
import org.skr.registry.PermissionRegistry;
import org.skr.registry.RealmRegistry;
import org.skr.registry.RegisterBatch;
import org.skr.registry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("unchecked")
public class RegistryLocalProxy implements RegistryProxy {

    @Autowired
    private RegistryService registryService;

    @Override
    public PermissionRegistry getPermission(String code) {
        return registryService.getPermission(code);
    }

    @Override
    public EndPointRegistry getEndPoint(String url) {
        return registryService.getEndPoint(url);
    }

    @Override
    public void registerRealm(RegisterBatch realmBatch) {
        registryService.registerRealm(realmBatch.realm, realmBatch.permissions, realmBatch.endPoints);
    }

    @Override
    public void unregisterRealm(String realmCode) {
        RealmRegistry realm = registryService.getRealm(realmCode);
        registryService.unregisterRealm(realm);
    }

    @Override
    public void revokePermission(String permissionCode) {
        registryService.revokePermission(permissionCode);
    }

    @Override
    public void revokeEndPoint(String url) {
        registryService.revokeEndPoint(url);
    }
}