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

import demo.skr.a.model.TaskExtension;
import lombok.NonNull;
import org.skr.common.util.Checker;
import org.skr.registry.AbstractRegHost;
import org.skr.registry.IRealm;
import org.skr.registry.SimpleRealm;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Controller
public class TaskRegHost extends AbstractRegHost<TaskRegistryPack> {

    private volatile static Map<String, TaskExtension> extensions
            = Collections.synchronizedMap(new HashMap<>());

    private volatile static Set<IRealm> registeredRealm
            = Collections.synchronizedSet(new HashSet<>());

    public IRealm getRealm(String realmCode) {
        return registeredRealm.stream().filter(realm -> realm.getCode().equals(realmCode)).findAny().orElse(null);
    }

    @Override
    protected void setRealmStatus(@NonNull String realmCode,
                                  IRealm.@NonNull RealmStatus status,
                                  Integer realmVersion,
                                  TaskRegistryPack registryPack) {
        if (status == IRealm.RealmStatus.STARTED) {
            SimpleRealm realm = SimpleRealm.of(realmCode);
            registeredRealm.add(realm);
        } else {
            registeredRealm.removeIf(realm -> realm.getCode().equals(realmCode));
        }
    }

    @Override
    protected void doRegister(@NonNull String realmCode, @NonNull TaskRegistryPack registryPack) {
        if (!Checker.isEmpty(registryPack.taskExtensions)) {
            registryPack.taskExtensions.forEach(taskExtension -> {
                extensions.put(taskExtension.name, taskExtension);
            });
        }
    }

    @Override
    protected void doUnregister(@NonNull String realmCode,
                                @NonNull TaskRegistryPack taskRegistryPack) {
        if (!Checker.isEmpty(taskRegistryPack.taskExtensions)) {
            taskRegistryPack.taskExtensions.forEach(taskExtension -> {
                extensions.remove(taskExtension.name);
            });
        }
    }

    @Override
    protected void doUninstall(@NonNull String realmCode) {
    }

    public List<TaskExtension> getExtensions() {
        return List.copyOf(extensions.values());
    }
}
