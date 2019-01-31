package org.skr.registry.controller;

import org.skr.common.exception.BizException;
import org.skr.common.exception.Errors;
import org.skr.common.util.BeanUtil;
import org.skr.registry.model.AppSvr;
import org.skr.registry.model.Permission;
import org.skr.registry.model.SiteUrl;
import org.skr.registry.repository.AppSvrRepository;
import org.skr.registry.repository.PermissionRepository;
import org.skr.registry.repository.SiteUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/registry")
public class RegistryController {

    @Autowired
    private AppSvrRepository appSvrRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private SiteUrlRepository siteUrlRepository;

    /** 注册服务 */
    @PostMapping("/appsvr")
    @Transactional
    public void registerAppSvr(@RequestBody AppSvr appSvr) {
        AppSvr existed = appSvrRepository.findOne(appSvr.code);
        if (existed == null) {
            appSvrRepository.save(appSvr);
        } else {
            BeanUtil.copyFields(appSvr, existed);
            appSvrRepository.save(existed);
        }
    }

    /** 注册权限 */
    @PostMapping("/permission/{appSvrCode}")
    @Transactional
    public void registerPermission(@PathVariable String appSvrCode,
                                   @RequestBody List<Permission> permissions) {
        AppSvr appSvr = appSvrRepository.findOne(appSvrCode);
        if (appSvr == null) {
            throw new BizException(Errors.REGISTRATION_ERROR.setMsg("AppSvr code %s", appSvrCode));
        }

        permissions.forEach(permission -> {
            Permission existed = permissionRepository.findOne(permission.code);
            if (existed == null) {
                permission.appSvr = appSvr;
                permissionRepository.save(permission);
            } else {
                if (!Objects.equals(existed.appSvr.code, appSvrCode)) {
                    throw new BizException(Errors.REGISTRATION_ERROR.setMsg(
                            "Permission %s is registered to %s", permission.code, existed.appSvr.code));
                }
                BeanUtil.copyFields(permission, existed, "code", "appSvr");
                permissionRepository.save(existed);
            }
        });
    }

    /** 获取权限集合 */
    @GetMapping("/permissions")
    public List<Permission> listPermissions() {
        return permissionRepository.findAll();
    }

    /** 获取权限 */
    @GetMapping("/permission/{code}")
    public Permission getPermission(@PathVariable String code) {
        return permissionRepository.findOne(code);
    }

    /** 注册siteMap */
    @PostMapping("/site-url/{appSvrCode}")
    @Transactional
    public void registerSiteUrl(@PathVariable String appSvrCode,
                                @RequestBody List<SiteUrl> SiteUrls) {
        AppSvr appSvr = appSvrRepository.findOne(appSvrCode);
        if (appSvr == null) {
            throw new BizException(Errors.REGISTRATION_ERROR.setMsg("AppSvr code %s", appSvrCode));
        }

        SiteUrls.forEach(siteUrl -> {
            SiteUrl existed = siteUrlRepository.findOne(siteUrl.url);
            if (existed == null) {
                siteUrlRepository.save(siteUrl);
            } else {
                if (!Objects.equals(existed.permission.appSvr.code, appSvrCode)) {
                    throw new BizException(Errors.REGISTRATION_ERROR.setMsg(
                            "SiteUrl %s is registered to %s", siteUrl.url, existed.permission.appSvr.code));
                }
                BeanUtil.copyFields(siteUrl, existed, "url", "permission");
                siteUrlRepository.save(existed);
            }
        });
    }

}
