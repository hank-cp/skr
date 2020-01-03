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

import lombok.NonNull;
import org.skr.common.exception.RegException;
import org.skr.common.util.Checker;
import org.skr.registry.service.IRegManager;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public abstract class AbstractRegManager<Realm extends IRealm> implements IRegManager<Realm> {

    protected volatile static Map<String, IRealm> activatedRealmCache
            = Collections.synchronizedMap(new HashMap<>());

    protected volatile static Map<String, IRegistrar> registrarCache
            = Collections.synchronizedMap(new HashMap<>());

    @Override
    public boolean register(@NonNull Realm realm) throws RegException {
        // if realm has been started, return
        if (activatedRealmCache.containsKey(realm.getName())) return false;

        // register Registrar
        if (!Checker.isEmpty(realm.getRegistrars())) {
            realm.getRegistrars().forEach(registrar -> {
                // registrar has been registered
                if (registrarCache.containsKey(registrar.getName())) {
                    throw new RegException();
                }
                registrarCache.put(registrar.getName(), registrar);
            });
        }

        // register Registry
        if (!Checker.isEmpty(realm.getRegistries())) {
            realm.getRegistries().forEach(registry -> {
                registrarCache.values().stream().filter(registrar -> {
                    return registrar.supports(registry);
                }).findAny().ifPresent(registrar -> {
                    registrar.register(registry);
                });
            });
        }

        return true;
    }

    @Override
    public boolean unregister(@NonNull String realmCode) throws RegException {
        Realm realm = (Realm) activatedRealmCache.get(realmCode);
        if (realm == null) {
            throw new RegException();
        }

        // register Registry
        if (!Checker.isEmpty(realm.getRegistries())) {
            realm.getRegistries().forEach(registry -> {
                registrarCache.values().stream().filter(registrar -> {
                    return registrar.supports(registry);
                }).findAny().ifPresent(registrar -> {
                    registrar.unregister(registry);
                });
            });
        }

        // register Registrar
        if (!Checker.isEmpty(realm.getRegistrars())) {
            realm.getRegistrars().forEach(registrar -> {
                registrarCache.remove(registrar.getName());
            });
        }

        // if realm has been started, return
        activatedRealmCache.remove(realm.getName());

        return true;
    }

    @Override
    public boolean uninstall(@NotNull Realm realm) throws RegException {
        return false;
    }
}