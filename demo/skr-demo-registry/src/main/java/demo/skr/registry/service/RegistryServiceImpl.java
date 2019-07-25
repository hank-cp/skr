package demo.skr.registry.service;

import demo.skr.registry.model.EndPoint;
import demo.skr.registry.model.Permission;
import demo.skr.registry.model.Realm;
import demo.skr.registry.repository.EndPointRepository;
import demo.skr.registry.repository.PermissionRepository;
import demo.skr.registry.repository.RealmRepository;
import org.skr.common.exception.BizException;
import org.skr.common.exception.Errors;
import org.skr.common.util.BeanUtil;
import org.skr.common.util.Checker;
import org.skr.common.util.tuple.Tuple3;
import org.skr.registry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Service
public class RegistryServiceImpl implements
        RegistryService<Realm, Permission, EndPoint> {

    @Autowired
    private RealmRepository realmRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private EndPointRepository endPointRepository;

    public Tuple3<Long, Long, Long> generatePermissionBits() {
        Permission permission = permissionRepository.findMaxBitPermission();
        if (permission == null) return new Tuple3<>(1L, 1L, 1L);

        long bit1 = permission.getBit1() << 1;
        long bit2 = permission.getBit2();
        long bit3 = permission.getBit3();
        if (bit1 > 0) return new Tuple3<>(bit1, bit2, bit3);

        // carry bit1 to bit2
        bit1 = 1;
        bit2 = bit2 << 1;
        if (bit2 > 0) return new Tuple3<>(bit1, bit2, bit3);

        // carry bit2 to bit3
        bit2 = 1;
        bit3 = bit3 << 1;
        return new Tuple3<>(bit1, bit2, bit3);
    }

    @Override
    public List<Realm> listRealms() {
        return realmRepository.findAll();
    }

    @Override
    public Realm getRealm(String realmCode) {
        return realmRepository.findById(realmCode).orElse(null);
    }

    @Override
    public List<Permission> listPermissions(Realm realm) {
        return permissionRepository.findEnabledPermission(realm);
    }

    @Override
    public Permission getPermission(String code) {
        return permissionRepository.findById(code).orElse(null);
    }

    @Override
    public List<EndPoint> listEndPoints(Realm realm) {
        return endPointRepository.findEnabledEndPoint(realm);
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
            Tuple3<Long, Long, Long> bits = generatePermissionBits();
            permission.bit1 = bits._0;
            permission.bit2 = bits._1;
            permission.bit3 = bits._2;
            permissionRepository.save(permission);
            return permission;
        } else {
            if (!Objects.equals(existed.getRealm().getCode(), realm.code)) {
                throw new BizException(Errors.REGISTRATION_ERROR.setMsg(
                        "Permission %s is registered to %s",
                        existed.code, existed.getRealm().getCode()));
            }
            BeanUtil.copyFields(permission, existed, "code", "realm");
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
                throw new BizException(Errors.REGISTRATION_ERROR.setMsg(
                        "EndPoint %s has been registered to %s",
                        endPoint.getUrl(), existed.getRealm().getCode()));
            }
            BeanUtil.copyFields(endPoint, existed, "url", "permission");
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
}
