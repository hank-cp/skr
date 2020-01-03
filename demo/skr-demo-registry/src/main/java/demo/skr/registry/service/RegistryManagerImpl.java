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

import demo.skr.registry.model.EndPoint;
import demo.skr.registry.model.Permission;
import demo.skr.registry.model.Realm;
import demo.skr.registry.repository.EndPointRepository;
import demo.skr.registry.repository.PermissionRepository;
import demo.skr.registry.repository.RealmRepository;
import org.skr.common.exception.BizException;
import org.skr.common.exception.ConfException;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.BeanUtil;
import org.skr.common.util.Checker;
import org.skr.registry.service.IRegManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Service
public class RegistryManagerImpl implements
        IRegManager<Realm, Permission, EndPoint> {

    @Autowired
    private RealmRepository realmRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private EndPointRepository endPointRepository;

    public Long generatePermissionBits() {
        Permission permission = permissionRepository.findMaxBitPermission();
        if (permission == null) return 1L;
        return permission.getBit() << 1;
    }

    public List<Realm> listRealms() {
        return realmRepository.findAll();
    }

    @Override
    public Realm getRealm(String realmCode) {
        return realmRepository.findById(realmCode).orElse(null);
    }

    @Override
    public List<Permission> listPermissions() {
        return permissionRepository.findEnabledPermissions();
    }

    @Override
    public Permission getPermission(String permissionCode) {
        return permissionRepository.findById(permissionCode).orElse(null);
    }

    @Override
    public List<EndPoint> listEndPoints() {
        return endPointRepository.findEnabledEndPoints();
    }

    @Override
    public EndPoint getEndPoint(String url) {
        return endPointRepository.findById(url).orElse(null);
    }

    @Override
    @Transactional
    public Realm registerRealm(@NotNull Realm realm,
                               @NotNull List<Permission> permissions,
                               @NotNull List<EndPoint> endPoints) {
        Realm existed = getRealm(realm.code);
        final Realm registeringRealm;
        realm.enabled = true;
        if (existed == null) {
            realmRepository.save(realm);
            registeringRealm = realm;
        } else {
            BeanUtil.copyFields(realm, existed);
            realmRepository.save(existed);
            registeringRealm = existed;
        }

        // unregister permission/endPoint not in this batch
        permissionRepository.findByRealm(registeringRealm).forEach(permission -> {
            if (permissions.stream().anyMatch(registeringPermission ->
                    Objects.equals(permission.code, registeringPermission.code))) return;
            permission.enabled = false;
            permissionRepository.save(permission);
        });
        endPointRepository.findByRealm(registeringRealm).forEach(endPoint -> {
            if (endPoints.stream().anyMatch(registeringEndPoint ->
                    Objects.equals(endPoint.url, registeringEndPoint.url))) return;
            endPoint.enabled = false;
            endPointRepository.save(endPoint);
        });

        permissions.forEach(permission -> {
            registerPermission(registeringRealm, permission);
        });

        endPoints.forEach(endPoint -> {
            registerEndPoint(registeringRealm, endPoint);
        });

        return realm;
    }

    @Transactional
    public Permission registerPermission(Realm realm, Permission permission) {
        Permission existed = getPermission(permission.getCode());
        permission.enabled = true;
        if (existed == null) {
            permission.realm = realm;
            // generate bits before save
            permission.bit = generatePermissionBits();
            permissionRepository.save(permission);
            return permission;
        } else {
            if (!Objects.equals(existed.getRealm().getCode(), realm.code)) {
                throw new BizException(ErrorInfo.PERMISSION_REGISTERED.msgArgs(
                        existed.code, existed.getRealm().getCode()));
            }
            BeanUtil.copyFieldsIncluding(permission, existed, "name", "vipLevel");
            permissionRepository.save(existed);
            return existed;
        }
    }

    @Transactional
    public EndPoint registerEndPoint(Realm realm, EndPoint endPoint) {
        EndPoint existed = getEndPoint(endPoint.getUrl());
        endPoint.enabled = true;
        if (existed == null) {
            endPoint.realm = realm;
            if (!Checker.isEmpty(endPoint.permissionCode)) {
                endPoint.permission = new Permission();
                endPoint.permission.code = endPoint.permissionCode;
            }
            endPointRepository.save(endPoint);
            return endPoint;
        } else {
            if (!Objects.equals(existed.getRealm().getCode(), realm.code)) {
                throw new BizException(ErrorInfo.END_POINT_REGISTERED.msgArgs(
                        endPoint.getUrl(), existed.getRealm().getCode()));
            }
            BeanUtil.copyFieldsIncluding(endPoint, existed, "description", "label", "breadcrumb");
            endPointRepository.save(existed);
            return existed;
        }
    }

    @Override
    @Transactional
    public void unregisterRealm(Realm realm) {
        realm = realmRepository.findById(realm.code).orElse(null);
        if (realm == null) return;
        realm.enabled = false;
        realmRepository.save(realm);

        permissionRepository.findByRealm(realm).forEach(permission -> {
            permission.enabled = false;
            permissionRepository.save(permission);
        });
        endPointRepository.findByRealm(realm).forEach(endPoint -> {
            endPoint.enabled = false;
            endPointRepository.save(endPoint);
        });
    }

    @Override
    public void revokePermission(String permissionCode) {
        Permission permission = getPermission(permissionCode);
        if (permission == null) return;
        if (permission.enabled) {
            throw new ConfException(ErrorInfo.PERMISSION_REVOKE_FAILED
                    .msgArgs(permissionCode, permission.realm.code));
        }
        permissionRepository.delete(permission);
    }

    @Override
    public void revokeEndPoint(String url) {
        EndPoint endPoint = getEndPoint(url);
        if (endPoint == null) return;
        if (endPoint.enabled) {
            throw new ConfException(ErrorInfo.END_POINT_REVOKE_FAILED
                    .msgArgs(url, endPoint.realm.code));
        }
        endPointRepository.delete(endPoint);
    }
}
