package org.skr.registry.service;

import org.skr.common.util.BeanUtil;
import org.skr.common.util.tuple.Tuple3;
import org.skr.registry.model.AppSvr;
import org.skr.registry.model.Permission;
import org.skr.registry.model.SiteUrl;
import org.skr.registry.repository.AppSvrRepository;
import org.skr.registry.repository.PermissionRepository;
import org.skr.registry.repository.SiteUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistryServiceImpl implements
        RegistryService<AppSvr, Permission, SiteUrl> {

    @Autowired
    private AppSvrRepository appSvrRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private SiteUrlRepository siteUrlRepository;

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
    public AppSvr getAppSvr(String code) {
        return appSvrRepository.findOne(code);
    }

    @Override
    public AppSvr saveAppSvr(AppSvr saving, AppSvr existed) {
        if (existed == null) {
            return appSvrRepository.save(saving);
        } else {
            BeanUtil.copyFields(saving, existed);
            return appSvrRepository.save(existed);
        }
    }

    @Override
    public Permission getPermission(String code) {
        return permissionRepository.findOne(code);
    }

    @Override
    public Permission savePermission(Permission saving, Permission existed) {
        if (existed == null) {
            return permissionRepository.save(saving);
        } else {
            BeanUtil.copyFields(saving, existed, "code", "appSvr");
            return permissionRepository.save(existed);
        }
    }

    @Override
    public SiteUrl getSiteUrl(String url) {
        return siteUrlRepository.findOne(url);
    }

    @Override
    public SiteUrl saveSiteUrl(SiteUrl saving, SiteUrl existed) {
        if (existed == null) {
            return siteUrlRepository.save(saving);
        } else {
            BeanUtil.copyFields(saving, existed, "url", "permission");
            return siteUrlRepository.save(existed);
        }
    }
}
