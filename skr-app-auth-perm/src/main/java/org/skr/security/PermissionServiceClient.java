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
package org.skr.security;

import org.skr.permission.IPermission;
import org.skr.permission.IPermissionService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@FeignClient(name = "${spring.skr.perm.host:registry}", qualifiers = "permissionService", primary = false)
public interface PermissionServiceClient extends IPermissionService {

    @Override
    @GetMapping("${spring.skr.perm.base-url:/registry}/permission/{code}")
    IPermission getPermission(@PathVariable String code);
}