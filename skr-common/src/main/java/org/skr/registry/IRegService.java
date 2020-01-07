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
package org.skr.registry;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public interface IRegService<RegistryPack extends IRegistryPack> {

	@GetMapping("${spring.skr.reg.base-url:/registry}/register/{realmCode}/{realmVersion}")
	void register(@PathVariable String realmCode,
				  @PathVariable int realmVersion,
				  @RequestBody RegistryPack realm);

	@GetMapping("${spring.skr.reg.base-url:/registry}/unregister/{realmCode}")
	void unregister(String realmCode);

	@GetMapping("${spring.skr.reg.base-url:/registry}/uninstall/{realmCode}")
	void uninstall(String realmCode);

}