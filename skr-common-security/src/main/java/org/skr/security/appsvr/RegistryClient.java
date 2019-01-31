
package org.skr.security.appsvr;

import lombok.*;
import org.skr.common.Constants;
import org.skr.security.PermissionDetail;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;

@FeignClient(name = "registry")
public interface RegistryClient {

    @Value(staticConstructor = "of") class AppSvr implements Serializable {
	    public String code;
	    public String name;
    }

    @ToString @EqualsAndHashCode @Getter
    @RequiredArgsConstructor(staticName = "of")
    @NoArgsConstructor
    class Permission implements PermissionDetail, Serializable {
		@NonNull public String code;
        @NonNull public String name;
		public long bit1;
		public long bit2;
		public long bit3;
		public int vipLevel;
    }

    @ToString @EqualsAndHashCode @Getter
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    class SiteUrl implements Serializable {
        public String url;
        public Permission permission;
        public String navPath;
        public String label;
    }

	@PostMapping("/registry/appsvr")
	void registerAppSvr(@RequestBody AppSvr appSvr);

	@PostMapping("/registry/permission/{appSvrCode}")
	void registerPermission(@PathVariable(name = "appSvrCode") String appSvrCode,
                            @RequestBody List<Permission> permissions);

	@GetMapping("/registry/permissions")
	List<Permission> listPermissions();

	@GetMapping("/registry/permission/{code}")
    @Cacheable(value = Constants.CACHE_NAME_DEFAULT,
            keyGenerator = "cacheKeyGenerator")
    Permission getPermission(@PathVariable(name = "code") String code);

	@PostMapping("/registry/site-url/{appSvrCode}")
	void registerSiteUrl(@PathVariable(name = "appSvrCode") String appSvrCode,
                         @RequestBody List<SiteUrl> SiteUrls);

}