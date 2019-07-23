package org.skr.registry.controller;

import org.skr.common.exception.BizException;
import org.skr.common.exception.Errors;
import org.skr.registry.model.EndPointRegistry;
import org.skr.registry.model.PermissionRegistry;
import org.skr.registry.model.RealmRegistry;
import org.skr.registry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/registry")
public class RegistryController {

    @Autowired
    private RegistryService registryService;

    @GetMapping("/realm/{realmCode}")
    public RealmRegistry getRealm(@PathVariable String realmCode) {
        return registryService.getRealm(realmCode);
    }

    @GetMapping("/realms")
    public List<RealmRegistry> listRealms() {
        return registryService.listRealms();
    }

    /** Register app service */
    @PostMapping("/realm")
    @Transactional
    public void registerRealm(@RequestBody RealmRegistry realm) {
        registryService.saveRealm(realm);
    }

    /** Get Permission */
    @GetMapping("/realm/{realmCode}/permissions")
    public List<PermissionRegistry> listPermissions(@PathVariable String realmCode) {
        RealmRegistry realm = registryService.getRealm(realmCode);
        return registryService.listPermissions(realm);
    }

    /** Get Permission */
    @GetMapping("/permission/{permissionCode}")
    public PermissionRegistry getPermission(@PathVariable String permissionCode) {
        return registryService.getPermission(permissionCode);
    }

    /** Register permission */
    @PostMapping("/permission/{realmCode}")
    @Transactional
    public void registerPermission(@PathVariable String realmCode,
                                   @RequestBody List<PermissionRegistry> permissions) {
        RealmRegistry realm = registryService.getRealm(realmCode);
        if (realm == null) {
            throw new BizException(Errors.REGISTRATION_ERROR.setMsg("Realm %s is not registered yet.", realmCode));
        }

        permissions.forEach(permission -> {
            registryService.savePermission(realm, permission);
        });
    }

    /** Register EndPoint */
    @PostMapping("/endpoint/{realmCode}")
    @Transactional
    public void registerEndPoint(@PathVariable String realmCode,
                                 @RequestBody List<EndPointRegistry> endPoints) {
        RealmRegistry realm = registryService.getRealm(realmCode);
        if (realm == null) {
            throw new BizException(Errors.REGISTRATION_ERROR.setMsg("Realm %s is not registered yet.", realmCode));
        }

        endPoints.forEach(endpoint -> {
            registryService.saveEndPoint(realm, endpoint);
        });
    }

}
