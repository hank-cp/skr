/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skr.registry.proxy;

import org.skr.registry.EndPointRegistry;
import org.skr.registry.PermissionRegistry;
import org.skr.registry.RegisterBatch;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@FeignClient(name = "${spring.skr.registry.host:registry}", qualifier = "registryProxy")
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
	@PostMapping("/registry/permission/{permissionCode}/revoke")
	void revokePermission(@PathVariable(name = "permissionCode") String permissionCode);

	/** Revoke a disabled EndPoint in order to reuse url. */
	@PostMapping("/registry/end-point/revoke")
	void revokeEndPoint(@RequestParam(name = "url") String url);
}