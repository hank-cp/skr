
package org.skr.registry.feign;

import org.skr.common.Constants;
import org.skr.registry.model.EndPointRegistry;
import org.skr.registry.model.PermissionRegistry;
import org.skr.registry.model.RealmRegistry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "registry")
public interface RegistryClient {

	@GetMapping("/registry/realm/{realmCode}")
	RealmRegistry getRealm(@PathVariable(name = "realmCode") String realmCode);

	@GetMapping("/registry/realms")
	List<RealmRegistry> listRealms();

	@PostMapping("/registry/realm")
	void registerRealm(@RequestBody RealmRegistry realm);

	@GetMapping("/registry/realm/{realmCode}/permissions")
	List<PermissionRegistry> listPermissions(@PathVariable(name = "realmCode") String realmCode);

	@GetMapping("/registry/permission/{permissionCode}")
	@Cacheable(value = Constants.CACHE_NAME_DEFAULT,
			keyGenerator = "cacheKeyGenerator")
	PermissionRegistry getPermission(@PathVariable(name = "permissionCode") String code);

	@PostMapping("/registry/permission/{realmCode}")
	void registerPermission(@PathVariable String realmCode,
							@RequestBody List<PermissionRegistry> permissions);

	@PostMapping("/registry/endpoint/{realmCode}")
	void registerEndPoint(@PathVariable(name = "realmCode") String realmCode,
                          @RequestBody List<EndPointRegistry> endPoints);

}