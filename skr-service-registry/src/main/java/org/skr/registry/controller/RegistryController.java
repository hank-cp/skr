/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skr.registry.controller;

import org.skr.registry.EndPointRegistry;
import org.skr.registry.PermissionRegistry;
import org.skr.registry.RealmRegistry;
import org.skr.registry.RegisterBatch;
import org.skr.registry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/registry")
public class RegistryController {

    @Autowired
    private RegistryService registryService;

    /** Get Permission */
    @GetMapping("/permission/{permissionCode}")
    public PermissionRegistry getPermission(@PathVariable String permissionCode) {
        return registryService.getPermission(permissionCode);
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

    /** Revoke a disabled EndPoint in order to reuse url. */
    @PostMapping("/end-point/revoke")
    @Transactional
    public void revokeEndPoint(@RequestParam String url) {
        registryService.revokePermission(url);
    }

}
