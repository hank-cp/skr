
package org.skr.registry.proxy;

import org.skr.registry.EndPointRegistry;
import org.skr.registry.PermissionRegistry;
import org.skr.registry.RealmRegistry;
import org.skr.registry.RegisterBatch;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "registry")
public interface RegistryProxy {

	@GetMapping("/registry/permission/{permissionCode}")
	PermissionRegistry getPermission(@PathVariable(name = "permissionCode") String code);

	/** Get Permission */
	@GetMapping("/registry/end-point/{url}")
	EndPointRegistry getEndPoint(@PathVariable(name = "url") String url);

	/** Register app service */
	@PostMapping("/registry/realm/register")
	void registerRealm(@RequestBody RegisterBatch realmBatch);

	/** Register app service */
	@PostMapping("/registry/realm/unregister/{realmCode}")
	void unregisterRealm(@PathVariable(name = "realmCode") String realmCode);

	/** Revoke a disabled permission in order to reuse permissionCode. */
	@PostMapping("/permission/{permissionCode}/revoke")
	void revokePermission(@PathVariable(name = "permissionCode") String permissionCode);

	/** Revoke a disabled EndPoint in order to reuse url. */
	@PostMapping("/end-point/revoke")
	@Transactional
	void revokeEndPoint(@RequestParam(name = "url") String url);
}