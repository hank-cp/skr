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
package demo.skr.registry.service;

import demo.skr.MyErrorInfo;
import demo.skr.reg.PermRegService;
import demo.skr.reg.PermRegistryPack;
import demo.skr.reg.model.EndPoint;
import demo.skr.reg.model.Permission;
import demo.skr.registry.model.PersistedEndPoint;
import demo.skr.registry.model.PersistedPermission;
import demo.skr.registry.model.PersistedRealm;
import demo.skr.registry.repository.EndPointRepository;
import demo.skr.registry.repository.PermissionRepository;
import demo.skr.registry.repository.RealmRepository;
import lombok.NonNull;
import org.skr.common.exception.BizException;
import org.skr.common.util.BeanUtil;
import org.skr.permission.IPermissionService;
import org.skr.registry.AbstractRegHost;
import org.skr.registry.IRealm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Objects;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@RestController
public class PermRegHost extends AbstractRegHost<PermRegistryPack>
        implements PermRegService, IPermissionService {

    @Autowired
    private RealmRepository realmRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private EndPointRepository endPointRepository;

    public Long generatePermissionBits() {
        PersistedPermission permission = permissionRepository.findMaxBitPermission();
        if (permission == null) return 1L;
        return permission.getBit() << 1;
    }

    public PersistedRealm getRealm(String code) {
        PersistedRealm realm = realmRepository.findById(code).orElse(null);
        if (realm == null) {
            realm = new PersistedRealm();
            realm.code = code;
        }
        realmRepository.save(realm);
        return realm;
    }

    @Override
    @GetMapping("${spring.skr.perm.base-url:/registry}/permission/{code}")
    public PersistedPermission getPermission(String code) {
        return permissionRepository.findById(code).orElse(null);
    }

    public PersistedEndPoint getEndPoint(String url) {
        return endPointRepository.findById(url).orElse(null);
    }

    @Override
    protected void setRealmStatus(@NonNull String realmCode,
                                  IRealm.@NonNull RealmStatus status,
                                  Integer realmVersion,
                                  PermRegistryPack registryPack) {
        PersistedRealm realm = getRealm(realmCode);
        if (realmVersion != null) {
            realm.version = realmVersion;
        }
        realm.status = status.value();
        realmRepository.save(realm);
    }

    @Override
    protected void doRegister(@NonNull String realmCode, @NonNull PermRegistryPack registryPack) {
        PersistedRealm realm = getRealm(realmCode);

        permissionRepository.findByRealm(realm).forEach(permission -> {
            if (registryPack.permissions.stream().anyMatch(registeringPermission ->
                    Objects.equals(permission.code, registeringPermission.code))) return;
            permission.disabled = true;
            permissionRepository.save(permission);
        });
        endPointRepository.findByRealm(realm).forEach(endPoint -> {
            if (registryPack.endPoints.stream().anyMatch(registeringEndPoint ->
                    Objects.equals(endPoint.url, registeringEndPoint.url))) return;
            endPoint.disabled = true;
            endPointRepository.save(endPoint);
        });

        registryPack.permissions.forEach(permission -> {
            registerPermission(realm, permission);
        });

        registryPack.endPoints.forEach(endPoint -> {
            registerEndPoint(realm, endPoint);
        });

    }

    public Permission registerPermission(PersistedRealm realm, Permission permission) {
        PersistedPermission savingPermission = getPermission(permission.code);
        if (savingPermission == null) {
            savingPermission = new PersistedPermission();
            BeanUtil.copyFields(permission, savingPermission);
            savingPermission.realm = realm;
            // generate bits before save
            permission.bit = generatePermissionBits();
        } else {
            if (!Objects.equals(savingPermission.realm.code, realm.code)) {
                throw new BizException(MyErrorInfo.PERMISSION_REGISTERED.msgArgs(
                        savingPermission.code, savingPermission.realm.code));
            }
            BeanUtil.copyFieldsIncluding(permission, savingPermission, "name", "vipLevel");
        }
        savingPermission.disabled = false;
        return permissionRepository.save(savingPermission);
    }

    public EndPoint registerEndPoint(PersistedRealm realm, EndPoint endPoint) {
        PersistedEndPoint savingEndPoint = getEndPoint(endPoint.url);
        if (savingEndPoint == null) {
            savingEndPoint = new PersistedEndPoint();
            BeanUtil.copyFields(endPoint, savingEndPoint);
            savingEndPoint.realm = realm;
            savingEndPoint.permission = new PersistedPermission();
            savingEndPoint.permission.code = endPoint.permissionCode;
        } else {
            if (!Objects.equals(savingEndPoint.realm.code, realm.code)) {
                throw new BizException(MyErrorInfo.END_POINT_REGISTERED.msgArgs(
                        savingEndPoint.url, savingEndPoint.realm.code));
            }
            BeanUtil.copyFieldsIncluding(endPoint, savingEndPoint, "description", "label", "breadcrumb");
        }
        savingEndPoint.disabled = false;
        return endPointRepository.save(savingEndPoint);
    }

    @Override
    protected void doUnregister(@NonNull String realmCode,
                                @NonNull PermRegistryPack permRegistryPack) {
    }

    @Override
    protected void doUninstall(@NonNull String realmCode) {
        PersistedRealm realm = getRealm(realmCode);
        if (realm == null) return;
        realmRepository.save(realm);

        permissionRepository.findByRealm(realm).forEach(permission -> {
            permission.disabled = true;
            permissionRepository.save(permission);
        });
        endPointRepository.findByRealm(realm).forEach(endPoint -> {
            endPoint.disabled = true;
            endPointRepository.save(endPoint);
        });
    }
}
