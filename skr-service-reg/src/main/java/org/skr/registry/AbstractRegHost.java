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
import lombok.extern.slf4j.Slf4j;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.exception.RegException;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Slf4j
public abstract class AbstractRegHost<RegistryPack extends IRegistryPack>
        implements IRegService<RegistryPack> {

    protected volatile static Map<String, IRegistryPack> startedRealmCache
            = Collections.synchronizedMap(new HashMap<>());

    protected abstract void setRealmStatus(@NonNull String realmCode,
                                           @NonNull IRealm.RealmStatus status,
                                           Integer realmVersion,
                                           RegistryPack registryPack);

    protected abstract void doRegister(@NonNull String realmCode,
                                       int realmVersion,
                                       @NonNull RegistryPack registryPack);

    protected abstract void doUnregister(@NonNull String realmCode,
                                         @NonNull RegistryPack registryPack);

    protected abstract void doUninstall(@NonNull String realmCode);

    @Override
    public final void register(@NonNull String realmCode,
                         int realmVersion,
                         @NonNull RegistryPack registryPack) {
        // if realm has been started, return
        if (startedRealmCache.containsKey(realmCode)) return;

        try {
            doRegister(realmCode, realmVersion, registryPack);
        } catch (Exception ex) {
            setRealmStatus(realmCode, IRealm.RealmStatus.ERROR, realmVersion, null);
            throw new RegException(ErrorInfo.REGISTER_REGISTRY_FAILED
                    .msgArgs(realmCode, ex.getMessage()), ex);
        }

        startedRealmCache.put(realmCode, registryPack);
        setRealmStatus(realmCode, IRealm.RealmStatus.STARTED, realmVersion, registryPack);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void unregister(@NonNull String realmCode) throws RegException {
        RegistryPack registryPack = (RegistryPack) startedRealmCache.get(realmCode);
        if (registryPack == null) {
            throw new RegException(ErrorInfo.REALM_NOT_REGISTERED.msgArgs(realmCode));
        }

        try {
            doUnregister(realmCode, registryPack);
        } catch (Exception ex) {
            throw new RegException(ErrorInfo.UNREGISTER_REGISTRY_FAILED
                    .msgArgs(realmCode, ex.getMessage()), ex);
        }

        startedRealmCache.remove(realmCode);
        setRealmStatus(realmCode, IRealm.RealmStatus.STOPPED, null, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void uninstall(@NotNull String realmCode) throws RegException {
        if (startedRealmCache.containsKey(realmCode)) {
            unregister(realmCode);
        }

        try {
            doUninstall(realmCode);
        } catch (Exception ex) {
            throw new RegException(ErrorInfo.UNINSTALL_REGISTRY_FAILED
                    .msgArgs(realmCode, ex.getMessage()), ex);
        }

        startedRealmCache.remove(realmCode);
        setRealmStatus(realmCode, IRealm.RealmStatus.UNINSTALLED, null, null);
    }
}