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
    @Transactional
    public Realm saveRealm(Realm saving) {
        Realm existed = getRealm(saving.code);
        if (existed == null) {
            return realmRepository.save(saving);
        } else {
            BeanUtil.copyFields(saving, existed);
            return realmRepository.save(existed);
        }
    }

    @Override
    public List<Permission> listPermissions(Realm realm) {
        return permissionRepository.findByRealm(realm);
    }

    @Override
    public Permission getPermission(String code) {
        return permissionRepository.findById(code).orElse(null);
    }

    @Override
    public Permission savePermission(Realm realm, Permission saving) {
        Permission existed = getPermission(saving.getCode());
        if (existed == null) {
            saving.realm = realm;
            // generate bits before save
            Tuple3<Long, Long, Long> bits = generatePermissionBits();
            saving.bit1 = bits._0;
            saving.bit2 = bits._1;
            saving.bit3 = bits._2;
            permissionRepository.save(saving);
            return saving;
        } else {
            if (!Objects.equals(existed.getRealm().getCode(), realm.code)) {
                throw new BizException(Errors.REGISTRATION_ERROR.setMsg(
                        "Permission %s is registered to %s",
                        existed.code, existed.getRealm().getCode()));
            }
            BeanUtil.copyFields(saving, existed, "code", "realm");
            permissionRepository.save(existed);
            return existed;
        }
    }

    public EndPoint getEndPoint(String url) {
        return endPointRepository.findById(url).orElse(null);
    }

    @Override
    @Transactional
    public EndPoint saveEndPoint(Realm realm, EndPoint saving) {
        EndPoint existed = getEndPoint(saving.getUrl());
        if (existed == null) {
            saving.realm = realm;
            if (!Checker.isEmpty(saving.permissionCode)) {
                saving.permission = new Permission();
                saving.permission.code = saving.permissionCode;
            }
            endPointRepository.save(saving);
            return saving;
        } else {
            if (!Objects.equals(existed.getRealm().getCode(), realm.code)) {
                throw new BizException(Errors.REGISTRATION_ERROR.setMsg(
                        "EndPoint %s has been registered to %s",
                        saving.getUrl(), existed.getRealm().getCode()));
            }
            BeanUtil.copyFields(saving, existed, "url", "permission");
            endPointRepository.save(existed);
            return existed;
        }
    }
}
