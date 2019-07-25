
package org.skr.registry.proxy;

import org.skr.registry.EndPointRegistry;
import org.skr.registry.PermissionRegistry;
import org.skr.registry.RealmRegistry;
import org.skr.registry.RegisterBatch;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "registry")
public interface RegistryProxy {

	@GetMapping("/registry/realm/{realmCode}")
	RealmRegistry getRealm(@PathVariable(name = "realmCode") String realmCode);

	@GetMapping("/registry/realms")
	List<RealmRegistry> listRealms();

	@GetMapping("/registry/realm/{realmCode}/permissions")
	List<PermissionRegistry> listPermissions(@PathVariable(name = "realmCode") String realmCode);

	@GetMapping("/registry/permission/{permissionCode}")
	PermissionRegistry getPermission(@PathVariable(name = "permissionCode") String code);

	@GetMapping("/registry/realm/{realmCode}/end-points")
	List<EndPointRegistry> listEndPoints(@PathVariable(name = "realmCode") String realmCode);

	/** Get Permission */
	@GetMapping("/registry/end-point/{url}")
	EndPointRegistry getEndPoint(@PathVariable(name = "url") String url);

	/** Register app service */
	@PostMapping("/registry/realm/register")
	void registerRealm(@RequestBody RegisterBatch realmBatch);

	/** Register app service */
	@PostMapping("/registry/realm/unregister/{realmCode}")
	void unregisterRealm(@PathVariable(name = "realmCode") String realmCode);

}