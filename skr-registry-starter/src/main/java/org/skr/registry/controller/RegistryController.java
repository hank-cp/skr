package org.skr.registry.controller;

import org.skr.common.exception.BizException;
import org.skr.common.exception.Errors;
import org.skr.registry.model.AppSvrRegistry;
import org.skr.registry.model.PermissionRegistry;
import org.skr.registry.model.SiteUrlRegistry;
import org.skr.registry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/registry")
public class RegistryController {

    @Autowired
    private RegistryService registryService;

    /** Register app service */
    @PostMapping("/appsvr")
    @Transactional
    public void registerAppSvr(@RequestBody AppSvrRegistry appSvr) {
        AppSvrRegistry existed = registryService.getAppSvr(appSvr.getCode());
        registryService.saveAppSvr(appSvr, existed);
    }

    /** Register permission */
    @PostMapping("/permission/{appSvrCode}")
    @Transactional
    public void registerPermission(@PathVariable String appSvrCode,
                                   @RequestBody List<PermissionRegistry> permissions) {
        AppSvrRegistry appSvr = registryService.getAppSvr(appSvrCode);
        if (appSvr == null) {
            throw new BizException(Errors.REGISTRATION_ERROR.setMsg("AppSvr code %s", appSvrCode));
        }

        permissions.forEach(permission -> {
            PermissionRegistry existed = registryService.getPermission(permission.getCode());
            if (existed == null) {
                permission.setAppSvr(appSvr);
                registryService.savePermission(permission, null);
            } else {
                if (!Objects.equals(existed.getAppSvr().getCode(), appSvrCode)) {
                    throw new BizException(Errors.REGISTRATION_ERROR.setMsg(
                            "Permission %s is registered to %s",
                            permission.getCode(), existed.getAppSvr().getCode()));
                }
                registryService.savePermission(permission, existed);
            }
        });
    }

    /** Get Permission */
    @GetMapping("/permission/{code}")
    public PermissionRegistry getPermission(@PathVariable String code) {
        return registryService.getPermission(code);
    }

    /** Register Site Url */
    @PostMapping("/site-url/{appSvrCode}")
    @Transactional
    public void registerSiteUrl(@PathVariable String appSvrCode,
                                @RequestBody List<SiteUrlRegistry> SiteUrls) {
        AppSvrRegistry appSvr = registryService.getAppSvr(appSvrCode);
        if (appSvr == null) {
            throw new BizException(Errors.REGISTRATION_ERROR.setMsg("AppSvr code %s", appSvrCode));
        }

        SiteUrls.forEach(siteUrl -> {
            SiteUrlRegistry existed = registryService.getSiteUrl(siteUrl.getUrl());
            if (existed == null) {
                registryService.saveSiteUrl(siteUrl, null);
            } else {
                if (!Objects.equals(existed.getPermission().getAppSvr().getCode(), appSvrCode)) {
                    throw new BizException(Errors.REGISTRATION_ERROR.setMsg(
                            "SiteUrl %s is registered to %s",
                            siteUrl.getUrl(), existed.getPermission().getAppSvr().getCode()));
                }
                registryService.saveSiteUrl(siteUrl, existed);
            }
        });
    }

}
