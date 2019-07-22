
package org.skr.registry.feign;

import org.skr.common.Constants;
import org.skr.registry.model.AppSvrRegistry;
import org.skr.registry.model.EndPointRegistry;
import org.skr.registry.model.PermissionRegistry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "registry")
public interface RegistryClient {

	@PostMapping("/registry/appsvr")
	void registerAppSvr(@RequestBody AppSvrRegistry appSvr);

	@PostMapping("/registry/permission/{appSvrCode}")
	void registerPermission(@PathVariable(name = "appSvrCode") String appSvrCode,
                            @RequestBody List<PermissionRegistry> permissions);

	@GetMapping("/registry/permission/{code}")
    @Cacheable(value = Constants.CACHE_NAME_DEFAULT,
            keyGenerator = "cacheKeyGenerator")
    PermissionRegistry getPermission(@PathVariable(name = "code") String code);

	@PostMapping("/registry/endpoint/{appSvrCode}")
	void registerEndPoint(@PathVariable(name = "appSvrCode") String appSvrCode,
                          @RequestBody List<EndPointRegistry> endPoints);

}