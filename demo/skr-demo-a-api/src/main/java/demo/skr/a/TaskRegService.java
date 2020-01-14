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
package demo.skr.a;

import lombok.NonNull;
import org.skr.common.exception.RegException;
import org.skr.registry.IRegService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@FeignClient(name = "demo-a", qualifier = "taskRegService", primary = false)
public interface TaskRegService extends IRegService<TaskRegistryPack> {

    @Override
    @PostMapping("/task-ext/register/{realmCode}/{realmVersion}")
    void register(@PathVariable @NonNull String realmCode,
                  @PathVariable int realmVersion,
                  @RequestBody @NonNull TaskRegistryPack realm);

    @Override
    @PostMapping("/task-ext/unregister/{realmCode}")
    void unregister(@PathVariable @NonNull String realmCode) throws RegException;

    @Override
    @PostMapping("/task-ext/uninstall/{realmCode}")
    void uninstall(@PathVariable @NonNull String realmCode);

}