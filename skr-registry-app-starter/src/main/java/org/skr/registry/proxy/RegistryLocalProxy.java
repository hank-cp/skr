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
package org.skr.registry.proxy;

import org.skr.registry.EndPointRegistry;
import org.skr.registry.PermissionRegistry;
import org.skr.registry.RealmRegistry;
import org.skr.registry.RegisterBatch;
import org.skr.registry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
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