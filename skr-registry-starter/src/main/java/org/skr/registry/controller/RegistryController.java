package org.skr.registry.controller;

import org.skr.registry.EndPointRegistry;
import org.skr.registry.PermissionRegistry;
import org.skr.registry.RealmRegistry;
import org.skr.registry.RegisterBatch;
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

    /** Get EndPoints */
    @GetMapping("/realm/{realmCode}/end-points")
    public List<EndPointRegistry> listEndPoints(@PathVariable String realmCode) {
        RealmRegistry realm = registryService.getRealm(realmCode);
        return registryService.listEndPoints(realm);
    }

    /** Get Permission */
    @GetMapping("/end-point/{url}")
    public EndPointRegistry getEndPoint(@PathVariable String url) {
        return registryService.getEndPoint(url);
    }

    /** Register app service */
    @PostMapping("/realm/register")
    @Transactional
    public void registerRealm(@RequestBody RegisterBatch realmBatch) {
        registryService.registerRealm(realmBatch.realm, realmBatch.permissions, realmBatch.endPoints);
    }

    /** Register app service */
    @PostMapping("/realm/unregister/{realmCode}")
    @Transactional
    public void unregisterRealm(@PathVariable String realmCode) {
        RealmRegistry realm = registryService.getRealm(realmCode);
        registryService.unregisterRealm(realm);
    }

    /** Revoke a disabled permission in order to reuse permissionCode. */
    @PostMapping("/permission/{permissionCode}/revoke")
    @Transactional
    public void revokePermission(@PathVariable String permissionCode) {
        registryService.revokePermission(permissionCode);
    }

}
