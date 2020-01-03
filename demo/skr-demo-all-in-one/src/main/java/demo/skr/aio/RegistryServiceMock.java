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
package demo.skr.aio;

import org.skr.registry.*;
import org.skr.registry.service.IRegManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@SuppressWarnings("unchecked")
public class RegistryServiceMock implements RegistryServiceClient {

    @Autowired
    private IRegManager registryManager;

    @Override
    public List<PermissionRegistry> listPermissions() {
        return registryManager.listPermissions();
    }

    @Override
    public PermissionRegistry getPermission(String code) {
        return registryManager.getPermission(code);
    }

    @Override
    public List<EndPointRegistry> listEndPoints() {
        return registryManager.listEndPoints();
    }

    @Override
    public EndPointRegistry getEndPoint(String url) {
        return registryManager.getEndPoint(url);
    }

    @Override
    public void registerRealm(RegisterBatch realmBatch) {
        registryManager.registerRealm(realmBatch.realm, realmBatch.permissions, realmBatch.endPoints);
    }

    @Override
    public void unregisterRealm(String realmCode) {
        RealmRegistry realm = registryManager.getRealm(realmCode);
        registryManager.unregisterRealm(realm);
    }

    @Override
    public void revokePermission(String permissionCode) {
        registryManager.revokePermission(permissionCode);
    }

    @Override
    public void revokeEndPoint(String url) {
        registryManager.revokeEndPoint(url);
    }
}